package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

public class Identity implements Transformation {
    private static final Logger LOG = Logger.getLogger(Identity.class.getName());

    @Override
    public Value evaluate(Value combinedInputs) {
        return combinedInputs;
    }

    @Override
    public Value differentiate(Value combinedInputs) {
        return Value.ONE;
    }

    @Override
    public ActivationFcn replaceWithSingleton() {
        return Singletons.identity;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(Singletons.identity);
    }

    public static class State extends Transformation.State {

        public State(Transformation transformation) {
            super(transformation);
        }

        /**
         * No functionality at all
         * @return
         */
        @Override
        public Value evaluate() {
            return input;
        }

        /**
         * No functionality
         * @param topGradient
         */
        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
        }
    }
}
