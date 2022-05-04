package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.logging.Logger;

/**
 * This is somewhere between activation and Aggregation...
 */
public class SharpMax extends Transformation {
    private static final Logger LOG = Logger.getLogger(SharpMax.class.getName());


    @Override
    public Value evaluate(Value combinedInputs) {
        if (combinedInputs instanceof VectorValue) {
            double[] maxValue = getMaxValue(((VectorValue) combinedInputs).values);
            return new VectorValue(maxValue);
        } else {
            LOG.severe("Cannot calculate SharpMax from more than one Value");
            return null;
        }
    }

    @Override
    public Value differentiate(Value combinedInputs) {
        return Value.ONE;
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


    @Override
    public boolean isInputSymmetric() {
        return true;
    }

    @Override
    public boolean isComplex() {
        return true;
    }


    public Aggregation replaceWithSingleton() {
        return Singletons.sharpmax;
    }

}
