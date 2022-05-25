package cz.cvut.fel.ida.algebra.functions.specific;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Count extends Aggregation {

    private static final Logger LOG = Logger.getLogger(Count.class.getName());

    public Count replaceWithSingleton() {
        return Singletons.count;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        return new ScalarValue(inputs.size());
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        LOG.warning("Propagating gradient through a COUNT fcn");
        return new ScalarValue(0.0);    //todo check
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }


}
