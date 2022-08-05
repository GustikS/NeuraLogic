package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Product implements Combination {
    private static final Logger LOG = Logger.getLogger(Product.class.getName());

    @Override
    public Combination replaceWithSingleton() {
        return Singletons.product;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value mult = inputs.get(0).clone();
        for (int i = 1; i < inputs.size(); i++) {
            mult = mult.times(inputs.get(i));
        }
        return mult;
    }

    @Override
    public boolean isPermutationInvariant() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(Singletons.product);
    }

    public static class State extends InputArrayState {

        public State(Combination combination) {
            super(combination);
        }

        @Override
        public Value evaluate() {
            return Singletons.product.evaluate(accumulatedInputs);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
        }

        @Override
        public Value nextInputDerivative() {
            return derivativeFrom(i++, processedGradient);
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
}
