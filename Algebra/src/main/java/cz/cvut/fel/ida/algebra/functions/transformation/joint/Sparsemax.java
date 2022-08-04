package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is both Transformation (joint activation) and Aggregation...
 */
public class Sparsemax extends Softmax {
    private static final Logger LOG = Logger.getLogger(Sparsemax.class.getName());

    @Override
    public Transformation replaceWithSingleton() {
        return Transformation.Singletons.sparsemax;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        double[] exps = getProbabilities(inputs);
        VectorValue output = new VectorValue(exps);
        return output;
    }

    @Override
    public double[] getProbabilities(double[] input) {
        double[] z_sorted = Arrays.copyOf(input, input.length);
        Arrays.sort(z_sorted);
        int length = z_sorted.length;
        double[] bound = new double[length];
        for (int i = 0; i < length; i++) {
            bound[i] = z_sorted[length - 1 - i] * (i + 1) + 1;
        }
        double sum = 0;
        double[] cumsum = new double[length];
        for (int i = 0; i < length; i++) {
            sum += z_sorted[z_sorted.length - 1 - i];
            cumsum[i] = sum;
        }
        int k = -1;
        double sparse_sum = 0;
        for (int i = 0; i < length; i++) {
            if (bound[i] > cumsum[i]) {
                k = i + 1;
                sparse_sum += z_sorted[length - 1 - i];
            }
        }

        double threshold = (sparse_sum - 1) / k;
        double[] out = new double[length];
        for (int i = 0; i < length; i++) {
            double val = input[i] - threshold;
            out[i] = val > 0 ? val : 0;
        }
        return out;
    }

    public double[] getProbabilities(List<Value> inputs) {
        if (inputs.size() == 1 && inputs.get(0) instanceof VectorValue) {
            return getProbabilities(((VectorValue) inputs.get(0)).values);
        }

        double[] z_values = new double[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            double val = ((ScalarValue) inputs.get(i)).value;
            z_values[i] = val;
        }
        return getProbabilities(z_values);
    }

    /**
     * todo test this actually (untested)
     *
     * @param exps
     * @return
     */
    public double[][] getGradient(double[] exps) {
        int support = 0;
        for (int i = 0; i < exps.length; i++) {
            if (exps[i] > 0) support++;
        }
        double[][] diffs = new double[exps.length][exps.length];
        for (int i = 0; i < exps.length; i++) {
            if (exps[i] == 0) continue;
            for (int j = 0; j < diffs.length; j++) {
                if (exps[j] == 0) continue;

                if (i == j) {
                    diffs[i][j] = 1 - 1.0 / support;
                } else {
                    diffs[i][j] = -1.0 / support;
                }
            }
        }
        return diffs;
    }

    @Override
    public boolean isInputSymmetric() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        if (singleInput)
            return new Sparsemax.TransformationState(Transformation.Singletons.sparsemax);
        else
            return new Sparsemax.CombinationState(Transformation.Singletons.sparsemax);
    }
}