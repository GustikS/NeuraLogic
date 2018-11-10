package networks.structure.metadata.states;

import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;

import java.util.logging.Logger;

public abstract class ActivationState implements Aggregation.State {
    private static final Logger LOG = Logger.getLogger(ActivationState.class.getName());

    public abstract Aggregation getActivation();


    public static class Standard extends ActivationState {
        Activation activation;
        Value summedInputs;

        public Standard(Activation activation, Value valueStore) {
            this.activation = activation;
            this.summedInputs = valueStore;
        }

        @Override
        public void cumulate(Value value) {
            summedInputs.increment(value);
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
            return activation.differentiate(summedInputs);
        }

        @Override
        public Aggregation getActivation() {
            return activation;
        }
    }

    public static abstract class Pooling extends ActivationState {
        Aggregation aggregation;

        public Pooling(Aggregation aggregation) {
            this.aggregation = aggregation;
        }

        @Override
        public Aggregation getActivation() {
            return aggregation;
        }

        public static class Max extends Pooling {
            int maxIndex = -1;
            int currentIndex = 0;
            Value maxValue;

            // these should obtain the function in construction to be more generic - builder should take care of that
            public Max(Aggregation aggregation) {
                super(aggregation);
            }

            @Override
            public void cumulate(Value value) {
                if (maxValue == null || value.greaterThan(maxValue)) {
                    maxValue = value;
                    maxIndex = currentIndex;
                }
                currentIndex++;
            }

            @Override
            public void invalidate() {
                maxIndex = -1;
                currentIndex = 0;
                maxValue = null;
            }

            @Override
            public int[] getInputMask() {
                int[] inputs = new int[0];
                inputs[0] = maxIndex;
                return inputs;
            }

            @Override
            public Value gradient() {
                return aggregation.differentiate(maxValue);
            }

        }

        public static class Avg extends Pooling {
            int count = 0;
            Value sum;

            // these should obtain the function in construction to be more generic - builder should take care of that
            public Avg(Aggregation aggregation, Value initSum) {
                super(aggregation);
                sum = initSum;
            }

            @Override
            public void cumulate(Value value) {
                sum.increment(value);
                count++;
            }

            @Override
            public void invalidate() {
                count = 0;
                sum.zero();
            }

            @Override
            public int[] getInputMask() {
                return null;
            }

            @Override
            public Value gradient() {
                return new ScalarValue(1/count);
            }
        }

        /**
         * todo whole class and corresponding aggregation function
         */
        public static class MaxK extends Pooling {


            public MaxK(int k) {
                super(null);
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
        }
    }
}