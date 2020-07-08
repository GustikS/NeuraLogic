package cz.cvut.fel.ida.neural.networks.structure.components.neurons.states;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSstack;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.Topologic;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.networks.ParentsTransfer;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.NeuronMapping;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Interfaces used for clarity of stateful processing during neural computations.
 * These are interfaces (not abstract classes) so that existing classes can be used directly as a state,
 * and so that compound states supporting multiple interfaces (e.g. dropout AND parents) can be created.
 * Special class States then contains particular formations of typical useful states.
 */
public interface State<V> extends Exportable {

    /**
     * Set all stateful values void.
     */
    void invalidate();

    /**
     * Apply default functionality carried by the given StateVisitor.
     *
     * @param visitor
     * @return
     */
    default V accept(StateVisiting<V> visitor) {
        return visitor.visit(this);
    }

    static State.Neural.Computation createBaseState(Settings settings, Aggregation activationFunction) {
        switch (settings.neuralState) {
            case STANDARD:
                return new States.ComputationStateStandard(activationFunction);
            case PARENTS:
                return new States.ParentCounter(activationFunction);
            case DROPOUT:
                return new States.DropoutStore(settings, activationFunction);
            case PAR_DROPOUT:
                return new States.DropoutStore(settings, activationFunction).new ParentsDropoutStore(activationFunction);
            default:
                return new States.ComputationStateStandard(activationFunction);
        }
    }

    /**
     * If there is minibatch, multiple threads will possibly be accessing the same neuron, i.e. we need array of states, one for each thread
     *
     * @param baseState
     * @param copies
     * @param <T>
     * @return
     */
    static <T extends Neural.Computation> States.ComputationStateComposite<T> createCompositeState(T baseState, int copies) {
        @SuppressWarnings("unchecked") final T[] batch = (T[]) Array.newInstance(baseState.getClass(), copies);
        States.ComputationStateComposite<T> stateComposite = new States.ComputationStateComposite<T>(batch);
        for (int i = 0; i < stateComposite.states.length; i++) {
            stateComposite.states[i] = (T) baseState.clone();
        }
        return stateComposite;
    }

    interface Neural<V> extends State<V>{

        /**
         * Each Neural State must have a Computation State!
         * Get Computation view on this neural State of a Neuron.
         *
         * @param index
         * @return
         */
        Computation getComputationView(int index);

        @NotNull
        Aggregation getAggregation();

        /**
         * Stateful values held by a Neuron for use during neural computation, i.e. Evaluation and Backpropagation
         */
        interface Computation extends Neural<Value> {

            Computation clone();

            void setupValueDimensions(Value value);

            AggregationState getAggregationState();

            Value getValue();

            Value getGradient();

            void setValue(Value value);

            void setGradient(Value gradient);

            void storeValue(Value value);

            void storeGradient(Value gradient);

            /**
             * tunnel for the composite state   - todo this is an (unneccessary) performance hotspot
             *
             * @param index
             * @return
             */
            @Override
            default Computation getComputationView(int index) {
                return this;
            }

            /**
             * Check using a State of the Neuron, whether this neuron is ready to propagate its result.
             * This only needs to be checked in {@link DFSstack} strategy,
             * whereas in {@link Topologic} the neurons are already ordered to be
             * visited when ready 4 expansion.
             *
             * @param visitor
             * @return
             */
            default boolean ready4expansion(StateVisiting visitor) {
                return true;
            }

            interface HasParents {

                int getParents(StateVisiting visitor);

                int getChecked(StateVisiting visitor);

                void setChecked(StateVisiting visitor, int checked);

                /**
                 * For setting up from State.Structure
                 *
                 * @param parentCount
                 */
                void setParents(StateVisiting visitor, int parentCount);
            }

            interface HasDropout {

                default void setStartingRate(Settings settings, int layer) {
                    setDropoutRate(settings.dropoutRate / layer);   //todo more sophisticated dropout rate strategies?
                }

                double getDropoutRate(StateVisiting visitor);

                void setDropoutRate(double rate);

                void setDropout(StateVisiting visitor);
            }

            interface Detailed {

                List<Value> getMessages();

                void setMessages(List<Value> values);

            }

        }

    }

    /**
     * Structural changes to neurons held as a state corresponding to each Neuron in a neural network.
     * These are not necessarily stored by a Neuron, but rather at a separate StatesCache.
     */
    interface Structure<V> extends State<V> {

        static StateVisiting.Computation getStatesInitializer(Settings settings) {
            return new ParentsTransfer(-1); //todo more
        }

        interface Parents {
            int getParentCount();

            void setParentCount(int parentCount);

            Neural<Value> getParentCounter();
        }

        interface InputNeuronMap extends Structure<NeuronMapping<Neurons>> {
            NeuronMapping<Neurons> getInputMapping();
        }

        interface WeightedInputsMap extends Structure<WeightedNeuronMapping<Neurons>> {
            WeightedNeuronMapping<Neurons> getWeightedMapping();
        }

        interface OutputNeuronMap extends Structure<NeuronMapping<Neurons>> {
            NeuronMapping<Neurons> getOutputMapping();
        }

    }
}