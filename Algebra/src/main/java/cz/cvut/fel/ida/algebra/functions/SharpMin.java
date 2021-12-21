package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.List;
import java.util.logging.Logger;

/**
 * This is somewhere between activation and Aggregation...
 */
public class SharpMin extends Activation {
    private static final Logger LOG = Logger.getLogger(SharpMin.class.getName());


    public Activation activation;

    public SharpMin(Activation activation) {
        super(activation.evaluation, activation.gradient);
        this.activation = activation;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value product = getMinValue(inputs);
        return activation.evaluate(product);
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        Value product = getMinValue(inputs);
        return activation.differentiate(product);
    }


    public Aggregation replaceWithSingleton() {
        LOG.severe("SharpMin cannot be singleton.");
        return null;
    }

    /**
     * returns a one-hot vector denoting the input maximum value (not just index)
     *
     * @param input
     * @return
     */
    public double[] getMinValue(double[] input) {
        double min = Double.MAX_VALUE;
        int min_index = -1;
        for (int i = 0; i < input.length; i++) {
            if (input[i] < min) {
                min = input[i];
                min_index = i;
            }
        }
        double[] result = new double[input.length];
        result[min_index] = min;
        return result;
    }

    /**
     * return a maximum Value from a list
     *
     * @param inputs
     * @return
     */
    public Value getMinValue(List<Value> inputs) {
        if (inputs.size() == 1 && inputs.get(0) instanceof VectorValue) {
            return new VectorValue(getMinValue(((VectorValue) inputs.get(0)).values));
        }

        Value min = new ScalarValue(Double.MAX_VALUE);
        for (int i = 0; i < inputs.size(); i++) {
            Value value = inputs.get(i);
            if (min.greaterThan(value)) {
                min = value;
            }
        }
        return min;
    }


}
