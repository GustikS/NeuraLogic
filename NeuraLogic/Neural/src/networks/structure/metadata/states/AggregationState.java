package networks.structure.metadata.states;

import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;

import java.util.*;
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

    public abstract Aggregation getAggregation();

    public abstract void setupValueDimensions(Value value);

    /**
     * State for standard Activation function, e.g. Sigmoid, which sums all the inputs and then applies some non-linearity to the result.
     */
    public static class ActivationState extends AggregationState {
        Activation activation;
        Value summedInputs;

        public ActivationState(Activation activation) {
            this.activation = activation;
        }

        public ActivationState(Activation activation, Value valueStore) {
            this.activation = activation;
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
            return activation.differentiate(summedInputs);
        }

        @Override
        public Value evaluate() {
            return activation.evaluate(summedInputs);
        }

        @Override
        public Activation getAggregation() {
            return activation;
        }

        @Override
        public void setupValueDimensions(Value value) {
            this.summedInputs = value.getForm();
        }
    }

    /**
     * State for aggregations based on pooling, e.g. Max or Avg. These require remembering different values for intermediate results.
     */
    public static abstract class Pooling extends AggregationState {
        Aggregation aggregation;

        public Pooling(Aggregation aggregation) {
            this.aggregation = aggregation;
        }

        @Override
        public Aggregation getAggregation() {
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
                return new ScalarValue(1);
            }

            @Override
            public Value evaluate() {
                return maxValue;
            }

            @Override
            public void setupValueDimensions(Value value) {
                this.maxValue = value.getForm();
            }
        }

        public static class Avg extends Pooling {
            int count = 0;
            Value sum;

            public Avg(Aggregation aggregation) {
                super(aggregation);
            }

            // these should obtain the function in construction to be more generic - builder should take care of that
            public Avg(Aggregation aggregation, Value initSum) {
                super(aggregation);
                sum = initSum;
            }

            @Override
            public void cumulate(Value value) {
                sum.incrementBy(value);
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
                return new ScalarValue(1.0 / count);
            }

            @Override
            public Value evaluate() {
                return sum.times(new ScalarValue(1.0 / count));
            }

            @Override
            public void setupValueDimensions(Value value) {
                this.sum = value.getForm();
            }
        }

        /**
         * MaxK is to return the average of max-k and propagate gradient into max-k inputs.
         * todo whole class and corresponding aggregation function
         */
        public static class MaxK extends Pooling {

            public MaxK(Aggregation aggregation, int k) {
                super(aggregation);
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
            public Value evaluate() {
                return null;
            }

            @Override
            public void setupValueDimensions(Value value) {

            }
        }
    }

    /**
     * In the most general case, we need to feed the whole list of inputs into the activation function, not just the sum.
     * There could possibly be functions that cannot be calculated in a cumulative fashion, e.g. when we need to process all elements twice (e.g. normalize/softmax ?),
     * Or with the special {@link networks.computation.evaluation.functions.CrossProduct} construct!
     */
    public static class CumulationState extends AggregationState {
        Aggregation aggregation;
        List<Value> accumulatedInputs;

        public CumulationState(Aggregation aggregation) {
            this.aggregation = aggregation;
            accumulatedInputs = new ArrayList<>();
        }

        @Override
        public Aggregation getAggregation() {
            return aggregation;
        }

        @Override
        public void setupValueDimensions(Value value) {

        }

        @Override
        public void cumulate(Value value) {
            accumulatedInputs.add(value);
        }

        @Override
        public void invalidate() {
            accumulatedInputs.clear();
        }

        @Override
        public int[] getInputMask() {
            return null;
        }

        @Override
        public Value gradient() {
            return aggregation.differentiate(accumulatedInputs);
        }

        @Override
        public Value evaluate() {
            return aggregation.evaluate(accumulatedInputs);
        }
    }

    public static class CrossProducState extends CumulationState {

        private static Map<Mapping, Mapping> cache = new HashMap<>();

        public int[][] mapping;
        int cross = 0;

        public CrossProducState(Aggregation aggregation) {
            super(aggregation);
        }

        public void initMapping(List<Value> inputValues) {
            int cross = 1;
            int[] sizes = new int[inputValues.size()];
            for (int i = 0; i < inputValues.size(); i++) {
                Value value = inputValues.get(i);
                int oneSize = 1;
                int[] size = value.size();
                for (int j = 0; j < size.length; j++) {
                    oneSize *= size[j];
                }
                sizes[i] = oneSize;
                cross *= oneSize;
            }
            mapping = new int[cross][inputValues.size()];
            combinations(0, new int[sizes.length], sizes);

            // compress duplicates
            Mapping wrap = new Mapping(this.mapping);
            Mapping load = cache.get(wrap);
            if (load != null) {
//                this.mapping = load.mapping; // free up memory
            } else {
                cache.put(wrap, wrap);
            }
        }

        private void combinations(int input, int[] current, int[] sizes) {
            if (input == sizes.length) {    //combi done
                System.arraycopy(current, 0, mapping[cross], 0, sizes.length);
                cross++;
                return;
            }
            for (int i = 0; i < sizes[input]; i++) {
                current[input] = i;
                combinations(input + 1, current, sizes);
            }
        }

        public static class Mapping {
            int[][] mapping;

            int hashcode = -1;

            public Mapping(int[][] mapping) {
                this.mapping = mapping;
            }

            @Override
            public int hashCode() {
                if (hashcode != -1) {
                    return hashcode;
                }
                hashcode = java.util.Arrays.deepHashCode(mapping);
                return hashcode;
            }

            @Override
            public boolean equals(Object obj) {
                return Arrays.deepEquals(mapping, ((Mapping) obj).mapping);
            }
        }
    }
}