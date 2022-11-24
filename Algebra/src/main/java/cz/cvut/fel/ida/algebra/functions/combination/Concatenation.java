package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * It flattens/linearizes the input Values, hence the result is always a VectorValue!
 * - to avoid the necessity to specify along which dimension to do the concat...       //todo make a parameterized version
 * <p>
 * Can be used also as an Aggregation now - i.e. combining variable-sized input sets - use with care,
 * since this means generally not knowing the output dimensionality of the corresponding neuron (+random, but constant, order of elements)
 */
public class Concatenation implements Combination, Aggregation {
    private static final Logger LOG = Logger.getLogger(Concatenation.class.getName());

    @Override
    public boolean isSplittable() {
        return true;
    }

    @Override
    public int[] aggregableTerms() {
        return new int[] {1};
    }

    @Override
    public Combination replaceWithSingleton() {
        return Combination.Singletons.concatenation;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        List<Double> concat = new ArrayList<>();
        for (Value input : inputs) {
            for (Double val : input) {
                concat.add(val);
            }
        }
        return new VectorValue(concat);
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        LOG.warning("Directly calculating derivative of CONCAT fcn");
        return Value.ONE;
    }

    @Override
    public Value evaluate(Value combinedInputs) {
        LOG.warning("Directly evaluating CONCAT fcn on a single input");
        return combinedInputs;  // no effect
    }

    @Override
    public Value differentiate(Value combinedInputs) {
        LOG.warning("Directly calculating derivative of CONCAT on a single input");
        return Value.ONE;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public boolean isPermutationInvariant() {
        return false;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(Combination.Singletons.concatenation);
    }

    public static class State extends Combination.InputArrayState {

        public State(Combination combination) {
            super(combination);
        }

        @Override
        public Value evaluate() {
            return Combination.Singletons.concatenation.evaluate(accumulatedInputs);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
        }

        /**
         * Just sending down the next scalar from the gradient vector
         *
         * @return
         */
        @Override
        public Value nextInputGradient() {
            return new ScalarValue(processedGradient.get(i++));
        }
    }
}
