package cz.cvut.fel.ida.algebra.functions.error;

import cz.cvut.fel.ida.algebra.functions.ErrorFcn;
import cz.cvut.fel.ida.algebra.functions.combination.Softmax;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.Sigmoid;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.math.VectorUtils;

import java.util.logging.Logger;

/**
 * A merge of Softmax/Sigmoid + Crossentropy functions (LogSumExp trick)
 * - has better numerical properties (stability)
 */
public class SoftEntropy implements ErrorFcn {
    private static final Logger LOG = Logger.getLogger(SoftEntropy.class.getName());

    static ScalarValue oneHalf = new ScalarValue(0.5);
    static ScalarValue one = new ScalarValue(1);
    static ScalarValue minusOne = new ScalarValue(-1);

    public static SoftEntropy singleton = new SoftEntropy();

    private static Softmax softmax = new Softmax();

    @Override
    public Value evaluate(Value logit, Value target) {
        //softmax
        if (logit instanceof ScalarValue) {    // binary case
            double logitVal = ((ScalarValue) logit).value;
            double targetVal = ((ScalarValue) target).value;
            double z;
            if (logitVal < 0) {      //for numeric stability only
                z = -(logitVal * targetVal - Math.log(1 + Math.exp(logitVal)));
            } else {
                z = -(logitVal * (targetVal - 1) - Math.log(Math.exp(-logitVal) + 1));
            }
            return new ScalarValue(z);

        } else if (logit instanceof VectorValue) {    // general (vector) crossentropy
            double[] logitV = ((VectorValue) logit).values;
            double[] targetV = ((VectorValue) target).values;

            double max = VectorUtils.max(logitV);    //for numeric stability

            double expsum = 0;
            double[] exps = new double[logitV.length];
            for (int i = 0; i < logitV.length; i++) {
                double exp = Math.exp(logitV[i] - max);
                exps[i] = exp;
                expsum += exp;
            }
            double logSoftmax = Math.log(expsum) + max;

            double[] logSumExp = new double[logitV.length];
            for (int i = 0; i < logSumExp.length; i++) {
                logSumExp[i] = logitV[i] - logSoftmax;
            }

            double err = 0;
            //xent
            for (int i = 0; i < targetV.length; i++) {
                err -= targetV[i] * logSumExp[i];
            }
            return new ScalarValue(err);
        } else {
            LOG.severe("Could no calculate SoftEntropy Value. Returning a dummy 0.");
            return new ScalarValue(0);
        }
    }

    @Override
    public Value differentiate(Value logit, Value target) {    //assuming 0/1 encoded targets!
        if (target instanceof ScalarValue) {    // binary crossentropy
            return target.minus(logit.apply(Sigmoid.logist));
        } else {
            VectorValue outputV = (VectorValue) logit;
            VectorValue targetV = (VectorValue) target;

            double[] exps = softmax.getProbabilities(outputV.values);

            double[] grad = new double[outputV.values.length];
            for (int i = 0; i < outputV.values.length; i++) {
                grad[i] = targetV.values[i] - exps[i];   // nice simplification w.r.t. separate softmax+Xent
            }
            return new VectorValue(grad);
        }
    }

    @Override
    public ErrorFcn getSingleton() {
        return singleton;
    }
}