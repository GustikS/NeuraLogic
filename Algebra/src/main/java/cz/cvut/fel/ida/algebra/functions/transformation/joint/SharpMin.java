package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.logging.Logger;

/**
 * This is somewhere between activation and Aggregation...
 */
public class SharpMin implements ActivationFcn.Combination, ActivationFcn.Transformation {
    private static final Logger LOG = Logger.getLogger(SharpMin.class.getName());


    @Override
    public Value evaluate(Value combinedInputs) {
        if (combinedInputs instanceof VectorValue) {
            double[] maxValue = getMinValue(((VectorValue) combinedInputs).values);
            return new VectorValue(maxValue);
        } else {
            LOG.severe("Cannot calculate SharpMin from more than one Value");
            return null;
        }
    }

    @Override
    public Value differentiate(Value combinedInputs) {
        return Value.ONE;
    }


    public Aggregation replaceWithSingleton() {
        return cz.cvut.fel.ida.algebra.functions.Transformation.Singletons.sharpmin;
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


    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }


}
