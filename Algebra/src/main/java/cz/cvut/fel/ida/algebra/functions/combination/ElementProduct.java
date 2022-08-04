package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

/**
 * Element-wise product of all the values with some activation function applied on top
 */
public class ElementProduct extends Product {
    private static final Logger LOG = Logger.getLogger(ElementProduct.class.getName());

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public Combination replaceWithSingleton() {
        return Singletons.elementProduct;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        int[] size = inputs.get(0).size();
        /*
        for (int i = 0; i < inputs.size(); i++) {
            if (!Arrays.equals(size,inputs.get(i).size())) {
                LOG.severe("ScalarProduct dimensions mismatch!");   //get maximal dimension here instead (zero-pad the rest) ? -> no, invalid vector/matrix operation anyway, rather do not misuse it
                return null;
            }
        }
        */
        Value mult = inputs.get(0).clone();
        for (int i = 1; i < inputs.size(); i++) {
            mult.elementMultiplyBy(inputs.get(i));
        }
        return mult;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(Singletons.elementProduct);
    }

    public static class State extends InputArrayState {

        Value combinedInputs;

        public State(Combination combination) {
            super(combination);
        }

        @Override
        public Value evaluate() {
            combinedInputs = Singletons.elementProduct.evaluate(accumulatedInputs);
            return combinedInputs;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            combinedInputs = null;
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
        }

        @Override
        public Value nextInputDerivative() {
//            Value gradient = combinedInputs.transposedView().elementDivideBy(accumulatedInputs.get(i++)); // check if transposition needed??
            Value gradient = combinedInputs.elementDivideBy(accumulatedInputs.get(i++));
            gradient.elementMultiplyBy(processedGradient);
            return gradient;
        }
    }
}
