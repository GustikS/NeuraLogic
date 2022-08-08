package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.transformation.joint.Identity;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;

/**
 * The most general interface for all activation functions
 * <p>
 * Steps for adding new activation/aggregation:
 * 1) add definition of the function (evaluation+differentiation) by overriding {@link Transformation} or {@link Combination} class
 * 2) create a static singleton in {@link Transformation} or {@link Combination} for reuse, if possible
 * 3) update {@link Settings#parseCombination(String)} or {@link Settings#parseTransformation(String)} with the new option
 * 4) if beneficial/required, create new State for the function in the same class
 * - see other similar function classes and copy
 * <p>
 * <p>
 * Created by gusta on 8.3.17.
 */
public interface ActivationFcn {
    static final Logger LOG = Logger.getLogger(ActivationFcn.class.getName());

    /**
     * Simply name of the activation function (used for external mapping into DL frameworks)
     */
    public default String getName() {
        return this.getClass().getSimpleName();
    }   //todo now now test if the name gets inherited

    /**
     * We do not want to create a new object for the same activation function that gets repeated over milions of neurons, even if it's very lightweight
     *
     * @return
     */
    public abstract ActivationFcn replaceWithSingleton();

    /**
     * Get the corresponding state for (faster) online calculation (if applicable)
     *
     * @param singleInput - whether to use Transformation or Combination interpretation of the function (e.g. SoftMax can be both)
     */
    public abstract State getState(boolean singleInput);

    /**
     * Return 2 values, lower bound and upper bound, beyond which the function is almost saturated
     *
     * @return
     */
    default Pair<Double, Double> getSaturationRange() {
        return null;    // by default there is no saturation (for Max, Avg, Identity, etc.)
    }

    /**
     * Just and ad-hoc flag for special functions
     *
     * @return
     */
    default boolean isComplex() {
        return false;
    }

    /**
     * The most general interface for all activation function States
     * <p>
     * This is to facilitate the fact that each function behaves differently w.r.t. online calculation, i.e. when inputs
     * are not all given at once, but need to be sequentially accumulated. For instance, when iterating inputs for the MAX
     * aggregation function, we need to remember the current maxValue and index, while for Sigmoid it is sufficient to
     * remember only the current sum of all processed inputs. Of course that this could be worked-around by just remembering
     * all the accumulated inputs (in a List) all the time, but that would be very inefficient, since the calculations would have to be
     * carried out by iterating the list many times (without remembering the intermediate results for each activation function).
     * <p>
     * During neural computation of Aggregation/Activation, a computational State resides in memory for efficient reuse.
     * E.g. we are not given all inputs/outputs at once, but the Values come sequentially as we iterate the neurons.
     * This State is then to accumulate the intermediate Values first, before finally calling the respective functions.
     * <p>
     * This interface applies equally to the Activation subclass, so it is just kept here once.
     * See AggregationState for concrete implementations.
     */
    public interface State extends Exportable {

        /**
         * Initialization of all necessary Values
         *
         * @param value
         */
        void setupDimensions(Value value);

        /**
         * Reset all intermediate results of calculation (after backprop step - typically by zeroing them out)
         */
        void invalidate();

        /**
         * Calculate the output Value of the current State.
         *
         * @return
         */
        Value evaluate();

        /**
         * Store a value - add it to the current state
         *
         * @param value
         */
        void cumulate(Value value);

        /**
         * Process the top gradient through the custom logic of this function state
         *
         * @param topGradient
         */
        void ingestTopGradient(Value topGradient);

        /**
         * Calculate gradient w.r.t. the next input - most general version
         * - for the standard activations, it will just keep returning the same processed top gradient
         *
         * @return
         */
        Value nextInputGradient();

        /**
         * If the activation applies to inly a subset of the input values, this return an array of the corresponding indices.
         * Otherwise returns null if the activation is based on all the inputs.
         *
         * @return
         */
        default int[] getInputMask() {
            return null;
        }

        Combination getCombination();

        Transformation getTransformation();

        static State getState(Combination combination, Transformation transformation) {
            State combinationState;
            State transformationState;

            if (combination == null && transformation == null) {
                LOG.severe("Trying to create a fcn state with no combination or transformation fcn");   // FactNeurons with no activation fcns get their states directly - not through this function
                return null;
            } else if (combination == null) {   // negation neurons (and maybe other single-input neurons in future)
                return transformation.getState(true);
            } else if (transformation == null || transformation instanceof Identity) {
                return combination.getState(false);     // aggregation neurons AND newly other neurons with no non-linearity on top
            } else {    //  neurons with both combination and transformation on top (e.g. SUM + Tanh)
                combinationState = combination.getState(false);
                transformationState = transformation.getState(true);
                return new CompoundState((Combination.State) combinationState, (Transformation.State) transformationState);
            }
        }
    }

    /**
     * A dummy state for FactNeurons (e.g. embeddings)
     * - can be learnable!
     */
    public static class SimpleValueState implements ActivationFcn.State {

        protected Value embedding;
        Value currentGradient;

        public SimpleValueState(Value embedding) {
            this.embedding = embedding;
        }

        @Override
        public void setupDimensions(Value value) {
            embedding = value.getForm();
        }

        @Override
        public void invalidate() {
            //void - this is a value storage - the value should stay throughout the whole learning
        }

        @Override
        public Value evaluate() {
            LOG.warning("Calling evaluate on SimpleValueState");
            return embedding;
        }

        @Override
        public void cumulate(Value value) {
            // no such thing here!
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            currentGradient = topGradient;
        }

        @Override
        public Value nextInputGradient() {
            return currentGradient; // need to pass, not for inputs (there are none), but for the offset of the FactNeuron which actually stores the embedding Value
        }

        @Override
        public Combination getCombination() {
            return null;
        }

        @Override
        public Transformation getTransformation() {
            return null;
        }

    }
}