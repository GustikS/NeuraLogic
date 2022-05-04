package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Sum extends Aggregation {

    private static final Logger LOG = Logger.getLogger(Sum.class.getName());

    public Sum replaceWithSingleton() {
        return Singletons.sum;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            sum.incrementBy(inputs.get(i));
        }
        return sum;
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        return Value.ONE;    //todo check
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }


}