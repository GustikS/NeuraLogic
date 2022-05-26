package cz.cvut.fel.ida.neural.networks.structure.components.neurons.states;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

/**
 * This is to facilitate the fact that each function behaves differently w.r.t. online calculation, i.e. when inputs
 * are not all given at once, but need to be sequentially accumulated. For instance, when iterating inputs for the MAX
 * aggregation function, we need to remember the current maxValue and index, while for Sigmoid it is sufficient to
 * remember only the current sum of all processed inputs. Of course that this could be worked-around by just remembering
 * all the accumulated inputs (in a List) all the time, but that would be very inefficient, since the calculations would have to be
 * carried out by iterating the list many times (without remembering the intermediate results for each activation function).
 */
public abstract class AggregationState implements Aggregation.State {
    private static final Logger LOG = Logger.getLogger(AggregationState.class.getName());

    Aggregation aggregation;
    Transformation transformation;
    TransformationState transformationState;
    Value combinedInputs;
    Value transformedGradient;

    public AggregationState(Transformation transformation) {
        this.transformation = transformation;
        this.transformationState = TransformationState.get(transformation);
    }

    public Aggregation getAggregation() {
        return aggregation;
    }

    public void setAggregation(Aggregation aggregation) {
        this.aggregation = aggregation;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }

    public int[] getInputMask() {
        return null;
    }

    @Override
    public void setupValueDimensions(Value value) {
        this.combinedInputs = value.getForm();
    }

    @Override
    public Value evaluate() {
        if (transformation != null)
            return transformation.evaluate(combinedInputs);
        else
            return combinedInputs;
    }

    @Override
    public Value gradient() {
        if (transformation != null) {
            transformedGradient = transformation.differentiate(combinedInputs);
        } else {
            transformedGradient = Value.ONE;
        }
        return transformedGradient;
    }

    /**
     * State for standard Activation function, e.g. Sigmoid, which sums all the inputs and then applies some non-linearity to the result.
     */
    public static class SumState extends AggregationState {

        public SumState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void cumulate(Value value) {
            combinedInputs.incrementBy(value);
        }

        @Override
        public void invalidate() {
            combinedInputs.zero();
//            transformedGradient.zero();       //not needed - assigned new every time
        }

        /**
         * The gradient is the same w.r.t. all the inputs here (symmetric case)
         *
         * @return
         */
        @Override
        public Value nextInputDerivative() {
            return transformedGradient;
        }

    }

    /**
     * A dummy state for fact neurons (e.g. embeddings)
     * - no value here, the value is stored straight in the States.SimpleValue state
     */
    public static class SimpleValueState implements Aggregation.State {

        @Override
        public void cumulate(Value value) {
            // no such thing here
        }

        @Override
        public void invalidate() {
//            value.zero(); //No, this is a value storage - the value can get initialized
        }

        @Override
        public int[] getInputMask() {
            return null;
        }

        @Override
        public Value gradient() {
            LOG.warning("Calling gradient of SimpleValueState");
            return Value.ONE;   //i.e. the invariant element for multiplication
        }

        @Override
        public Value nextInputDerivative() {
            LOG.warning("Calling nextInputDerivative of SimpleValueState");
            return Value.ONE;   //i.e. the invariant element for multiplication
        }

        @Override
        public void setupValueDimensions(Value value) {

        }

        @Override
        public Value evaluate() {
            LOG.warning("Calling evaluate of SimpleValueState");
            return null;
        }

        //        @Override
//        public Activation getAggregation() {
//            return null;
//        }
//
//        @Override
//        public void setAggregation(Aggregation act) {
//        }
//
//        @Override
//        public Transformation getTransformation() {
//            return null;
//        }
//
//        @Override
//        public void setTransformation(Transformation transformation) {
//
//        }
////
//        @Override
//        public void setupValueDimensions(Value value) {
//            LOG.warning("Calling setupValueDimensions of SimpleValueState");
//        }
    }


}