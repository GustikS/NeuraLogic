package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

public class Product extends Activation {
    private static final Logger LOG = Logger.getLogger(Product.class.getName());

    @Override
    public String getName() {
        return "Product";
    }

    Activation activation;

    public Product(Activation activation) {
        super(activation.evaluation, activation.gradient);
        this.activation = activation;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        LOG.severe("Product cannot be singleton.");
        return null;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value product = multiplyInputs(inputs);
        return activation.evaluate(product);
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        Value product = multiplyInputs(inputs);
        return activation.differentiate(product);
    }

    protected Value multiplyInputs(List<Value> inputs) {
        Value mult = inputs.get(0).clone();
        for (int i = 1; i < inputs.size(); i++) {
            mult = mult.times(inputs.get(i));
        }
        return mult;
    }
}
