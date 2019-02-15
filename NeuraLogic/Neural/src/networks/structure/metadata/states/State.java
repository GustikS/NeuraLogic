package networks.structure.metadata.states;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.inputMappings.NeuronMapping;
import networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import org.jetbrains.annotations.NotNull;
import settings.Settings;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Interfaces used for clarity of stateful processing during neural computations.
 * These are interfaces (not abstract classes) so that existing classes can be used directly as a state,
 * and so that compound states supporting multiple interfaces (e.g. dropout AND parents) can be created.
 * Special class States then contains particular formations of typical useful states.
 */
public interface State<V> {

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

    interface Neural<V> extends State<V> {

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

            Value getResult(StateVisiting<Value> visitor);

            void setResult(StateVisiting<Value> visitor, Value value);

            void store(StateVisiting<Value> visitor, Value value);

            /**
             * tunnel for the composite state
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
             * This only needs to be checked in {@link networks.computation.iteration.modes.DFSstack} strategy,
             * whereas in {@link networks.computation.iteration.modes.Topologic} the neurons are already ordered to be
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
                double getDropout(StateVisiting visitor);

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

        interface Parents {
            int getParentCount();

            void setParentCount(int parentCount);
        }

        interface InputNeuronMap extends Structure<NeuronMapping<Neuron>> {
            NeuronMapping<Neuron> getInputMapping();
        }

        interface WeightedInputsMap extends Structure<WeightedNeuronMapping<Neuron>> {
            WeightedNeuronMapping<Neuron> getWeightedMapping();
        }

        interface OutputNeuronMap extends Structure<NeuronMapping<Neuron>> {
            NeuronMapping<Neuron> getOutputMapping();
        }

    }
}