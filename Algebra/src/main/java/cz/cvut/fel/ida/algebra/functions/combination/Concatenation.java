package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
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
 */
public class Concatenation implements Combination {
    private static final Logger LOG = Logger.getLogger(Concatenation.class.getName());

    @Override
    public Combination replaceWithSingleton() {
        return Singletons.concatenation;
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
    public boolean isComplex() {
        return true;
    }

    @Override
    public boolean isPermutationInvariant() {
        return false;
    }


    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(Singletons.concatenation);
    }

    public static class State extends Combination.InputArrayState {

        public State(Combination combination) {
            super(combination);
        }

        @Override
        public Value evaluate() {
            return Singletons.concatenation.evaluate(accumulatedInputs);
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
