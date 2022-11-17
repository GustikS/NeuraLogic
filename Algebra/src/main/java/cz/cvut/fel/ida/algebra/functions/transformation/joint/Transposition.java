package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

public class Transposition implements Transformation {
    private static final Logger LOG = Logger.getLogger(Transposition.class.getName());

    @Override
    public ActivationFcn replaceWithSingleton() {
        return Singletons.transposition;
    }

    public Value evaluate(Value combinedInputs) {
        return combinedInputs.transposedView();
    }

    /**
     * Constant identity gradient 1.0 of the same dimensionality
     *
     * Inefficient, but it should not be called anyway...see the State
     *
     * @param combinedInputs
     * @return
     */
    public Value differentiate(Value combinedInputs) {
        Value form = combinedInputs.getForm();
//        form.transpose();     //we want the derivative to be addable to the inputs, not the (transposed) outputs
        Value apply = form.apply(in -> 1.0);
        return apply;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(Singletons.transposition);
    }

    @Override
    public boolean changesShape() {
        return true;
    }

    public static class State extends Transformation.State {

        public State(Transformation transformation) {
            super(transformation);
        }

        /**
         * The only functionality of Transposition derivative...
         * @param topGradient
         */
        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient.transposedView();
        }
    }
}
