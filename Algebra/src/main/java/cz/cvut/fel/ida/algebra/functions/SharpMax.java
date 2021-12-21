package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.List;
import java.util.logging.Logger;

/**
 * This is somewhere between activation and Aggregation...
 */
public class SharpMax extends Activation {
    private static final Logger LOG = Logger.getLogger(SharpMax.class.getName());


    public Activation activation;

    public SharpMax(Activation activation) {
        super(activation.evaluation, activation.gradient);
        this.activation = activation;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value product = getMaxValue(inputs);
        return activation.evaluate(product);
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        Value product = getMaxValue(inputs);
        return activation.differentiate(product);
    }


    public Aggregation replaceWithSingleton() {
        LOG.severe("SharpMax cannot be singleton.");
        return null;
    }

    /**
     * returns a one-hot vector denoting the input maximum value (not just index)
     *
     * @param input
     * @return
     */
    public double[] getMaxValue(double[] input) {
        double max = Double.MIN_VALUE;
        int max_index = -1;
        for (int i = 0; i < input.length; i++) {
            if (input[i] > max) {
                max = input[i];
                max_index = i;
            }
        }
        double[] result = new double[input.length];
        result[max_index] = max;
        return result;
    }

    /**
     * return a maximum Value from a list
     *
     * @param inputs
     * @return
     */
    public Value getMaxValue(List<Value> inputs) {
        if (inputs.size() == 1 && inputs.get(0) instanceof VectorValue) {
            return new VectorValue(getMaxValue(((VectorValue) inputs.get(0)).values));
        }

        Value max = new ScalarValue(Double.MIN_VALUE);
        for (int i = 0; i < inputs.size(); i++) {
            Value value = inputs.get(i);
            if (value.greaterThan(max)) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }

}
