package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

public class Transposition extends Activation {
    private static final Logger LOG = Logger.getLogger(Transposition.class.getName());


    public Transposition() {
        super(null, null);
    }

    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        return Singletons.transposition;
    }

    public Value evaluate(Value summedInputs) {
        return summedInputs.transposedView();
    }

    /**
     * constant identity gradient 1.0 of the same dimensionality
     * @param summedInputs
     * @return
     */
    public Value differentiate(Value summedInputs) {
        Value form = summedInputs.getForm();
        form.transpose();
        Value apply = form.apply(in -> 1.0);
        return apply;
    }
}
