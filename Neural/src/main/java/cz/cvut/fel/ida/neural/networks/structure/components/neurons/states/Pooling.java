package cz.cvut.fel.ida.neural.networks.structure.components.neurons.states;

import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

/**
 * State for aggregations based on pooling, e.g. Max or Avg. These require remembering different values for intermediate results.
 */
public abstract class Pooling extends AggregationState {
    private static final Logger LOG = Logger.getLogger(Pooling.class.getName());

    public Pooling() {
        super(null);
    }

    public Pooling(Transformation transformation) {
        super(transformation);
    }


    public static class Max extends Pooling {
        int maxIndex = -1;
        int currentIndex = 0;
//        Value maxValue;

        // these should obtain the function in construction to be more generic - builder should take care of that
        public Max(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void cumulate(Value value) {
            if (combinedInputs == null || value.greaterThan(combinedInputs)) {
                combinedInputs = value;
                maxIndex = currentIndex;
            }
            currentIndex++;
        }

        @Override
        public void invalidate() {
            maxIndex = -1;
            currentIndex = 0;
            combinedInputs = null;
        }

        @Override
        public int[] getInputMask() {
            int[] inputs = new int[1];
            inputs[0] = maxIndex;
            return inputs;
        }

        @Override
        public Value nextInputDerivative() {
            LOG.warning("Calling nextInputDerivative in Max pooling");
            return null;
        }

    }


    public static class Min extends Pooling {
        int minIndex = -1;
        int currentIndex = 0;

        public Min(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void cumulate(Value value) {
            if (combinedInputs == null || combinedInputs.greaterThan(value)) {
                combinedInputs = value;
                minIndex = currentIndex;
            }
            currentIndex++;
        }

        @Override
        public void invalidate() {
            minIndex = -1;
            currentIndex = 0;
            combinedInputs = null;
        }

        @Override
        public int[] getInputMask() {
            int[] inputs = new int[1];
            inputs[0] = minIndex;
            return inputs;
        }

        @Override
        public Value nextInputDerivative() {
            return null;
        }

    }


    public static class Avg extends Pooling {
        int count = 0;
        ScalarValue inverseCount;

        public Avg(Transformation transformation) {
            super(transformation);
        }

        public Avg(Transformation transformation, Value initSum) {
            super(transformation);
            combinedInputs = initSum;
        }

        @Override
        public void cumulate(Value value) {
            combinedInputs.incrementBy(value);
            count++;
        }

        @Override
        public void invalidate() {
            combinedInputs.zero();
            inverseCount = new ScalarValue(1.0 / count);
            count = 0;    //it will be the same every time but anyway...
        }

        @Override
        public Value gradient() {
            return inverseCount;
        }

        @Override
        public Value nextInputDerivative() {
            return inverseCount;
        }

        @Override
        public Value evaluate() {
//            return sum.apply(x -> x / count);
            return combinedInputs.times(inverseCount);
        }

        @Override
        public void setupValueDimensions(Value value) {
            this.combinedInputs = value.getForm();
        }
    }

    public static class Sum extends Pooling {

        public Sum(Transformation transformation) {
            super(transformation);
        }

        public Sum(Transformation transformation, Value initSum) {
            super(transformation);
            combinedInputs = initSum;
        }

        @Override
        public void cumulate(Value value) {
            combinedInputs.incrementBy(value);
        }

        @Override
        public void invalidate() {
            combinedInputs.zero();
        }

        @Override
        public Value nextInputDerivative() {
            return Value.ONE;
        }

    }

    /**
     * MaxK is to return the average of max-k and propagate gradient into max-k inputs.
     * todo whole class and corresponding aggregation function
     */
    public static class MaxK extends Pooling {

        public MaxK(Transformation transformation, int k) {
            super(transformation);
            //todo
        }

        @Override
        public void cumulate(Value value) {

        }

        @Override
        public void invalidate() {

        }

        @Override
        public int[] getInputMask() {
            return new int[0];
        }

        @Override
        public Value gradient() {
            return null;
        }

        @Override
        public Value nextInputDerivative() {
            return null;
        }

        @Override
        public Value evaluate() {
            return null;
        }

        @Override
        public void setupValueDimensions(Value value) {

        }
    }
}
