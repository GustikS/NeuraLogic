package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.List;
import java.util.logging.Logger;

/**
 * todo This is somewhere between activation and Aggregation...
 */
public class Softmax extends Activation implements XMax {
    private static final Logger LOG = Logger.getLogger(Softmax.class.getName());

    public Softmax() {
        super(null, null);
    }

    @Override
    public Aggregation replaceWithSingleton() {
        return Singletons.softmax;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        double[] exps = getProbabilities(inputs);
        VectorValue output = new VectorValue(exps);
        return output;
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        double[] exps = getProbabilities(inputs);
        double[] diffs = getGradient(exps);
        VectorValue output = new VectorValue(diffs);
        return output;
    }

    public double[] getGradient(double[] exps) {
        double[] diffs = new double[exps.length];
        for (int i = 0; i < exps.length; i++) {
            for (int j = 0; j < diffs.length; j++) {
                if (i == j) {
                    diffs[j] += exps[i] * (1 - exps[j]);
                } else {
                    diffs[j] -= exps[i] * exps[j];
                }
            }
        }
        return diffs;
    }

    @Override
    public double[] getProbabilities(double[] input) {
        double expsum = 0;
        double[] exps = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            double exp = Math.exp(input[i]);
            exps[i] = exp;
            expsum += exp;
        }
        for (int i = 0; i < exps.length; i++) {
            exps[i] /= expsum;
        }
        return exps;
    }

    public double[] getProbabilities(List<Value> inputs) {
        if (inputs.size()==1 && inputs.get(0) instanceof VectorValue){
            return getProbabilities(((VectorValue) inputs.get(0)).values);
        }

        double expsum = 0;
        double[] exps = new double[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            double exp = Math.exp(((ScalarValue) inputs.get(i)).value);
            exps[i] = exp;
            expsum += exp;
        }
        for (int i = 0; i < exps.length; i++) {
            exps[i] /= expsum;
        }
        return exps;
    }

    @Override
    public boolean isInputSymmetric() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return true;
    }
}
