package cz.cvut.fel.ida.algebra.functions.states;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;


public abstract class AggregationState implements Aggregation.State {
    private static final Logger LOG = Logger.getLogger(AggregationState.class.getName());

    Value combinedInputs;

    public Pooling(){}

    @Override
    public int[] getInputMask() {
        return null;
    }

    /**
     * State for standard Activation function, e.g. Sigmoid, which sums all the inputs and then applies some non-linearity to the result.
     */
    public static class ActivationState extends AggregationState {
        ElementWise elementWise;
        Value summedInputs;

        public ActivationState(ElementWise elementWise) {
            this.elementWise = elementWise;
        }

        public ActivationState(ElementWise elementWise, Value valueStore) {
            this.elementWise = elementWise;
            this.summedInputs = valueStore;
        }

        @Override
        public void cumulate(Value value) {
            summedInputs.incrementBy(value);
        }

        @Override
        public void invalidate() {
            summedInputs.zero();
        }

        public int[] getInputMask() {
            return null;
        }

        @Override
        public Value gradient() {
            return elementWise.differentiate(summedInputs);
        }

        @Override
        public Value evaluate() {
            return elementWise.evaluate(summedInputs);
        }

        @Override
        public ElementWise getCombination() {
            return elementWise;
        }

        @Override
        public void setCombination(Aggregation act) {
            this.elementWise = (ElementWise) act;
        }

        @Override
        public void setupDimensions(Value value) {
            this.summedInputs = value.getForm();
        }
    }

    /**
     * A dummy state for fact neurons (e.g. embeddings)
     * - no value here, the value is stored straight in the States.SimpleValue state
     */
    public static class SimpleValueState extends AggregationState {

        public SimpleValueState() {
        }


        @Override
        public void cumulate(Value value) {
            // no such thing here
        }

        @Override
        public void invalidate() {
//            value.zero(); //No, this is a value storage - the value can get initialized
        }

        public int[] getInputMask() {
            return null;
        }

        @Override
        public Value gradient() {
            return Value.ONE;   //i.e. the invariant element for multiplication
        }

        @Override
        public Value evaluate() {
            return null;
        }

        @Override
        public ElementWise getCombination() {
            return null;
        }

        @Override
        public void setCombination(Aggregation act) {
        }

        @Override
        public void setupDimensions(Value value) {

        }
    }

    /**
     * Same as ActivationState, but multiplies the inputs instead
     * todo now Could be a subclass, but test if not causing a slowdown
     */
    public static class ElementProductState extends AggregationState {
        ElementWise elementWise;
        Value multipliedInputs;

        public ElementProductState(ElementWise elementWise) {
            this.elementWise = elementWise;
        }

        public ElementProductState(ElementWise elementWise, Value valueStore) {
            this.elementWise = elementWise;
            this.multipliedInputs = valueStore;
        }

        @Override
        public void cumulate(Value value) {
            multipliedInputs.elementMultiplyBy(value);
        }

        @Override
        public void invalidate() {
            multipliedInputs.zero().incrementBy(Value.ONE);
        }

        public int[] getInputMask() {
            return null;
        }

        @Override
        public Value gradient() {
            return elementWise.differentiate(multipliedInputs);
        }

        @Override
        public Value evaluate() {
            return elementWise.evaluate(multipliedInputs);
        }

        @Override
        public ElementWise getCombination() {
            return elementWise;
        }

        @Override
        public void setCombination(Aggregation act) {
            this.elementWise = (ElementWise) act;
        }

        @Override
        public void setupDimensions(Value value) {
            this.multipliedInputs = value.getForm();
        }
    }

    /**
     * Trivial but necessary to correctly handle changing dimensions
     */
    public static class TranspositionState extends AggregationState {
        Value inputs;
        ElementWise transp = ElementWise.Singletons.transposition;

        public TranspositionState() {
        }

        @Override
        public void cumulate(Value value) {
            inputs = value;
        }

        @Override
        public void invalidate() {
            inputs = null;
        }

        public int[] getInputMask() {
            return null;
        }

        @Override
        public Value gradient() {
            return transp.differentiate(inputs);
        }

        @Override
        public Value evaluate() {
            return transp.evaluate(inputs);
        }

        @Override
        public ElementWise getCombination() {
            return transp;
        }

        @Override
        public void setCombination(Aggregation act) {
            this.transp = (ElementWise) act;
        }

        @Override
        public void setupDimensions(Value value) {
            // do nothing - there is no actual cummulation of values
        }
    }

    /**
     * State for aggregations based on pooling, e.g. Max or Avg. These require remembering different values for intermediate results.
     */
    public static abstract class Pooling extends AggregationState {
        Aggregation aggregation;
    Transformation transformation;
    TransformationState transformationState;
    Value combinedInputs;
    Value transformedGradient;

    public AggregationState(Transformation transformation) {
        this.transformation = transformation;
        this.transformationState = TransformationState.get(transformation);
    }

    public Aggregation getCombination() {
        return aggregation;
    }

    public void setCombination(Aggregation aggregation) {
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
    public void setupDimensions(Value value) {
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
        public void setupDimensions(Value value) {

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