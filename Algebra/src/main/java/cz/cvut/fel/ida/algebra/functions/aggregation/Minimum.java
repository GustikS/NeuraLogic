package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Minimum extends Aggregation {
    private static final Logger LOG = Logger.getLogger(Minimum.class.getName());

    @Override
    public Minimum replaceWithSingleton() {
        return Singletons.minimum;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value min = inputs.get(0);
        for (int i = 1; i < inputs.size(); i++) {
            Value value;
            if (min.greaterThan(value = inputs.get(i))) {
                min = value;
            }
        }
        return min;
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        return Value.ONE;  //todo check
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }


}
