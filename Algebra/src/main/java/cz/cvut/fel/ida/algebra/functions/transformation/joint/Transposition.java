package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.functions.states.AggregationState;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

public class Transposition extends Transformation {
    private static final Logger LOG = Logger.getLogger(Transposition.class.getName());

    @Override
    public ActivationFcn replaceWithSingleton() {
        return Singletons.transposition;
    }

    @Override
    public State getState(ActivationFcn activationFcn) {
        return new AggregationState.TranspositionState();
    }

    public Value evaluate(Value combinedInputs) {
        return combinedInputs.transposedView();
    }

    /**
     * constant identity gradient 1.0 of the same dimensionality
     *
     * @param summedInputs
     * @return
     */
    public Value differentiate(Value summedInputs) {
        Value form = summedInputs.getForm();
//        form.transpose();     //we want the derivative to be addable to the inputs, not the (transposed) outputs
        Value apply = form.apply(in -> 1.0);
        return apply;
    }
}
