package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.*;
import java.util.logging.Logger;

/**
 * Takes all combinations of dimensions of all inputs -> sum into a long vector -> activation
 * This is a possible rule neuron's activation function!
 * <p>
 * todo if too slow in evaluation, precalculate the final vector via pointers to the underyling values via special aggregationState?
 */
public class CrossSum implements Combination {
    private static final Logger LOG = Logger.getLogger(CrossSum.class.getName());

    @Override
    public Combination replaceWithSingleton() {
        return Singletons.crossSum;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        List<Value> values = new ArrayList<>(inputs.size());
        values.addAll(inputs);
        List<Double> outputVector = new ArrayList<>();
        combinationsRecursive(outputVector, 0.0, values);
        return new VectorValue(outputVector);
    }

    /**
     * All combinations of dimensions of all inputs -> long vector
     *
     * @param output
     * @param sum
     * @param values
     */
    private void combinationsRecursive(List<Double> output, double sum, List<Value> values) {
        if (values.size() == 0) {
            output.add(sum);
            return;
        }
        Value removed = values.remove(0);
        for (Double next : removed) {
            sum += next;
            combinationsRecursive(output, sum, values);
            sum -= next;
        }
        values.add(0, removed);
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public boolean isPermutationInvariant() {
        return false;   // changing the number of inputs in crossproduct is problematic...
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(Singletons.crossSum);
    }

    public static class State extends Combination.InputArrayState {

        private static Map<Mapping, Mapping> cache = new HashMap<>();

        public int[][] mapping;
        int cross = 0;

        List<Value> inputGradients;

        public State(Combination combination) {
            super(combination);
        }

        @Override
        public Value initEval(List<Value> values) {
            Value eval = super.initEval(values);

            if (accumulatedInputs == null || accumulatedInputs.isEmpty()){
                LOG.severe("CrossSum State not  initialized correctly");
            }

            initMapping(accumulatedInputs);

            if (inputGradients == null){
                inputGradients = new ArrayList<>(accumulatedInputs.size());
                for (int j = 0; j < accumulatedInputs.size(); j++) {
                    inputGradients.add(accumulatedInputs.get(i).getForm());
                }
            }

            return eval;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            for (Value inputGradient : inputGradients) {
                inputGradient.zero();
            }
        }

        @Override
        public Value evaluate() {
            return Combination.Singletons.crossSum.evaluate(accumulatedInputs);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
//            processedGradient = topGradient;

            for (int i = 0; i < mapping.length; i++) {
                double grad = processedGradient.get(i);
                int[] map = mapping[i];

                for (int j = 0; j < inputGradients.size(); j++) {
                    Value inGrad = inputGradients.get(j);
                    inGrad.increment(map[j], grad);
                }
            }
        }

        @Override
        public Value nextInputGradient() {
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
