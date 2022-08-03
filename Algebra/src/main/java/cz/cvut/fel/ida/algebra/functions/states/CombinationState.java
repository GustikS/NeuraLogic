package cz.cvut.fel.ida.algebra.functions.states;

import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.functions.combination.Concatenation;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.*;
import java.util.logging.Logger;

/**
 * In the most general case, we need to feed the whole list of inputs into the activation function, not just the sum.
 * There could possibly be functions that cannot be calculated in a cumulative fashion, e.g. when we need to process all elements twice (e.g. normalize/softmax ?),
 * Or with the special CrossSum construct!
 */
public abstract class CombinationState implements Combination.State {
    private static final Logger LOG = Logger.getLogger(CombinationState.class.getName());

    ArrayList<Value> accumulatedInputs;
    int i = 0;

    public CombinationState() {
        accumulatedInputs = new ArrayList<>();
    }

    @Override
    public void cumulate(Value value) {
        accumulatedInputs.add(value);
    }

    @Override
    public void invalidate() {
//            accumulatedInputs.trimToSize();
        accumulatedInputs.clear();          //todo test with "new" initialization instead and pass the number of inputs
        i = 0;
    }

    @Override
    public Value evaluate() {
        combine();
        return super.evaluate();
    }

    @Override
    public Value gradient() {
        transformedGradient = super.gradient();
        Value combinationGradient = derive();
        if (transformedGradient.getClass().equals(combinationGradient.getClass())) {
            transformedGradient = transformedGradient.elementTimes(combinationGradient);  //elementTimes here - since the fcn to be differentiated was applied element-wise on a vector
        } else {
            transformedGradient = transformedGradient.transposedView().times(combinationGradient);  //times here - since the fcn was a complex vector function (e.g. softmax) and has a matrix derivative (Jacobian)
        }
        return transformedGradient;
    }

    abstract void combine();

    /**
     * Derivative/gradient of the combination/aggregation function (beneath the activation/transformation)
     *
     * @return
     */
    abstract Value derive();


    public static class ConcatState extends CombinationState {

        public ConcatState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            combinedInputs = null;
        }

        /**
         * Just sending down the next scalar from the gradient vector
         *
         * @return
         */
        @Override
        public Value nextInputDerivative() {
            return new ScalarValue(transformedGradient.get(i++));
        }

        @Override
        public void combine() {
            if (combinedInputs == null) {
                combinedInputs = Concatenation.concatenate(accumulatedInputs);
            } else {
                LOG.warning("Calling ConcatState combine more than once");
            }
        }

        @Override
        Value derive() {
            return Value.ONE;
        }
    }

    public static class SoftmaxState extends CombinationState {
        double[] probabilities;

        public SoftmaxState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            probabilities = null;
        }

        @Override
        public void combine() {
//            if (combinedInputs == null) {
            probabilities = Transformation.Singletons.softmax.getProbabilities(accumulatedInputs);
            combinedInputs = new VectorValue(probabilities);
//            }
        }

        @Override
        public Value derive() {
            double[][] gradient = Transformation.Singletons.softmax.getGradient(probabilities);
            return new MatrixValue(gradient);
        }

        @Override
        public Value nextInputDerivative() {
            return new ScalarValue(transformedGradient.get(i++));
        }
    }

    /**
     * Same as ActivationState, but multiplies the inputs instead
     */
    public static class ElementProductState extends CombinationState {

        public ElementProductState(Transformation activation) {
            super(activation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            combinedInputs.zero().incrementBy(Value.ONE);
        }

        @Override
        void combine() {
            combinedInputs = Combination.Singletons.elementProduct.multiplyInputs(accumulatedInputs);
        }

        @Override
        Value derive() {
            return Value.ONE;
        }

        @Override
        public Value nextInputDerivative() {
            return combinedInputs.transposedView().elementDivideBy(accumulatedInputs.get(i++));
        }
    }

    public static class ProductState extends CombinationState {


        public ProductState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            combinedInputs = null;
        }

        @Override
        void combine() {
            if (combinedInputs == null) {
                combinedInputs = accumulatedInputs.get(0).clone();
                for (int i = 1; i < accumulatedInputs.size(); i++) {
                    combinedInputs = combinedInputs.times(accumulatedInputs.get(i));
                }
            }
        }

        @Override
        Value derive() {
            return Value.ONE;
        }

        @Override
        public Value nextInputDerivative() {
            return derivativeFrom(i++, transformedGradient);
        }

        public Value derivativeFrom(int index, Value topGradient) {
            int size = accumulatedInputs.size();

            Value left = null;
            Value right = null;

            for (int i = 0; i < size; i++) {        //todo make more efficient by precomputing an array of all the products
                if (i == index) {
                    continue;
                } else if (i < index) {
                    if (left == null) {
                        left = accumulatedInputs.get(i);
                    } else {
                        left = left.times(accumulatedInputs.get(i));
                    }
                } else {
                    if (right == null) {
                        right = accumulatedInputs.get(i);
                    } else {
                        right = right.times(accumulatedInputs.get(i));
                    }
                }
            }
            if (left == null) {
                Value transposedRight = right.transposedView();
                return topGradient.times(transposedRight);
            } else if (right == null) {
                return topGradient.transposedView().times(left);
            } else {
                Value times = topGradient.transposedView().times(left);
                Value kronecker = right.transposedView().kroneckerTimes(times);   //todo test this!!
                return kronecker;
            }
        }
    }

    public static class CrossSumState extends CombinationState {

        private static Map<Mapping, Mapping> cache = new HashMap<>();

        public int[][] mapping;
        int cross = 0;

        List<Value> inputGradients;

        public CrossSumState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            combinedInputs = null;
            for (Value inputGradient : inputGradients) {
                inputGradient.zero();
            }
        }

        @Override
        void combine() {
            combinedInputs = Combination.Singletons.crossSum.evaluate(accumulatedInputs);
        }

        @Override
        Value derive() {
            if (inputGradients == null){
                inputGradients = new ArrayList<>(accumulatedInputs.size());
                for (int j = 0; j < accumulatedInputs.size(); j++) {
                    inputGradients.add(accumulatedInputs.get(i).getForm());
                }
            }

            for (int i = 0; i < mapping.length; i++) {
                double grad = transformedGradient.get(i);
                int[] map = mapping[i];

                for (int j = 0; j < inputGradients.size(); j++) {
                    Value inGrad = inputGradients.get(j);
                    inGrad.increment(map[j], grad);
                }
            }
            return Value.ONE;       //the function itself is just summation
        }

        @Override
        public Value nextInputDerivative() {
            return inputGradients.get(i++);
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
