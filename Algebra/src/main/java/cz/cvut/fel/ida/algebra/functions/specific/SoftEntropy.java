package cz.cvut.fel.ida.algebra.functions.specific;

import cz.cvut.fel.ida.algebra.functions.ErrorFcn;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.logging.Logger;

/**
 * A merge of Softmax + Crossentropy functions
 * - has better numerical properties (and is faster)
 */
public class SoftEntropy implements ErrorFcn {
    private static final Logger LOG = Logger.getLogger(SoftEntropy.class.getName());

    static ScalarValue oneHalf = new ScalarValue(0.5);
    static ScalarValue one = new ScalarValue(1);
    static ScalarValue minusOne = new ScalarValue(-1);

    public static SoftEntropy singleton = new SoftEntropy();

    @Override
    public Value evaluate(Value logit, Value target) {
        if (logit instanceof ScalarValue) {    // binary case
            double logitVal = ((ScalarValue) logit).value;
            double z;
            if (target.greaterThan(oneHalf)) {
                z = Math.log(Math.exp(-logitVal) + 1);
            } else {
                z = Math.log(Math.exp(logitVal) + 1);
            }
            return new ScalarValue(z);
        } else {    // general (vector) crossentropy
            double[] logitV = ((VectorValue) logit).values;
            double[] targetV = ((VectorValue) target).values;

            double expsum = 0;
            double[] exps = new double[logitV.length];      //todo now add LogSumExp trick here for numeric stability
            for (int i = 0; i < logitV.length; i++) {
                double exp = Math.exp(logitV[i]);
                exps[i] = exp;
                expsum += exp;
            }
            for (int i = 0; i < exps.length; i++) {
                exps[i] /= expsum;
            }
            double err = 0;
            for (int i = 0; i < targetV.length; i++) {
                err -= targetV[i] * Math.log(exps[i]);
            }
            return new ScalarValue(err);
        }
    }

    @Override
    public Value differentiate(Value logit, Value target) {    //assuming 0/1 target!
        if (target instanceof ScalarValue) {    // binary crossentropy
            double logitVal = ((ScalarValue) logit).value;
            double diff;
            if (target.greaterThan(oneHalf)) {
                diff = 1 - logitVal;
            } else {
                diff = -logitVal;
            }
            return new ScalarValue(diff);
        } else {
            VectorValue outputV = (VectorValue) logit;
            VectorValue targetV = (VectorValue) target;
            double[] grad = new double[outputV.values.length];
            for (int i = 0; i < outputV.values.length; i++) {
                grad[i] = targetV.values[i] - outputV.values[i];   // nice simplification w.r.t. separate softmax+Xent
            }
            return new VectorValue(grad);
        }
    }

    @Override
    public ErrorFcn getSingleton() {
        return singleton;
    }
}