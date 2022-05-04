package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Product extends Combination {
    private static final Logger LOG = Logger.getLogger(Product.class.getName());


    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        return Singletons.product;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value product = multiplyInputs(inputs);
        return product;
    }

    @Override
    public boolean isInputSymmetric() {
        return false;
    }

    protected Value multiplyInputs(List<Value> inputs) {
        Value mult = inputs.get(0).clone();
        for (int i = 1; i < inputs.size(); i++) {
            mult = mult.times(inputs.get(i));
        }
        return mult;
    }
}
