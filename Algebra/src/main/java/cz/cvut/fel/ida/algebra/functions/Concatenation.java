package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Concatenation extends Activation {
    private static final Logger LOG = Logger.getLogger(Concatenation.class.getName());

    private final Activation activation;


    @Override
    public String getName() {
        return "Concat";
    }

    protected Concatenation(Activation activation) {
        super(activation.evaluation, activation.gradient);
        this.activation = activation;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        LOG.severe("Concat cannot be singleton (may have different Activations)");
        return null;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        VectorValue conc = concatenate(inputs);
        return activation.evaluate(conc);
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        VectorValue conc = concatenate(inputs);
        return activation.differentiate(conc);
    }

    public static VectorValue concatenate(List<Value> inputs) {
        List<Double> concat = new ArrayList<>();
        for (Value input : inputs) {
            for (Double val : input) {
                concat.add(val);
            }
        }
        return new VectorValue(concat);
    }
}
