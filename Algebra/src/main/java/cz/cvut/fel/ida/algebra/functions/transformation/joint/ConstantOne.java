package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

/**
 * A special class for a function outputting a constant ONE
 */
public class ConstantOne implements Transformation {
    private static final Logger LOG = Logger.getLogger(ConstantOne.class.getName());

    State singletonState = new State(Singletons.constantOne);

    @Override
    public Value evaluate(Value combinedInputs) {
        return Value.ONE;
    }

    @Override
    public Value differentiate(Value combinedInputs) {
        return Value.ZERO;
    }

    @Override
    public ActivationFcn replaceWithSingleton() {
        return Singletons.constantOne;
    }

    /**
     * There is actually nothing stateful here
     * @return
     */
    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return singletonState;
    }

    public static class State extends Transformation.State {

        public State(Transformation transformation) {
            super(transformation);
        }

        /**
         * Return constant one no matter what
         * @return
         */
        @Override
        public Value evaluate() {
            return Value.ONE;
        }

        /**
         * the gradient will be zero no matter what
         * @param topGradient
         */
        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = Value.ZERO;
        }
    }
}
