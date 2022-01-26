package cz.cvut.fel.ida.algebra.functions.specific;

import cz.cvut.fel.ida.algebra.functions.ErrorFcn;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.logging.Logger;

public class Crossentropy implements ErrorFcn {
    private static final Logger LOG = Logger.getLogger(Crossentropy.class.getName());

    static ScalarValue oneHalf = new ScalarValue(0.5);
    static ScalarValue one = new ScalarValue(1);
    static ScalarValue minusOne = new ScalarValue(-1);

    static double MAXENTVALUE = 1e2;
    static double MAXENTGRADIENT = 1e10;

    public static Crossentropy singleton = new Crossentropy();

    @Override
    public Value evaluate(Value output, Value target) {
        if (target instanceof ScalarValue) {    // binary crossentropy
            if (target.greaterThan(oneHalf)) {
                return output.apply(x -> x > 0 ? -Math.log(x) : MAXENTVALUE);
            } else {
                return output.apply(x -> x < 1 ? -Math.log(1 - x) : MAXENTVALUE);
            }
        } else {    // general crossentropy
            VectorValue outputV = (VectorValue) output;
            VectorValue targetV = (VectorValue) target;
            double err = 0;
            for (int i = 0; i < outputV.values.length; i++) { // full loop to account possibly even for multiple labels at the same time
                err -= targetV.values[i] * (outputV.values[i] > 0 ? Math.log(outputV.values[i]) : -MAXENTVALUE);
            }
            return new ScalarValue(err);
        }
    }

    @Override
    public Value differentiate(Value output, Value target) {    //assuming 0/1 target!
        if (target instanceof ScalarValue) {    // binary crossentropy
            if (target.greaterThan(oneHalf)) {
                return output.apply(x -> x > 0 ? 1 / x : MAXENTGRADIENT);
            } else {
                return output.apply(x -> x < 1 ? -1 / (1 - x) : -MAXENTGRADIENT);
            }
        } else {    // general crossentropy
            VectorValue outputV = (VectorValue) output;
            VectorValue targetV = (VectorValue) target;
            double[] grad = new double[outputV.values.length];
            for (int i = 0; i < outputV.values.length; i++) {
                if (targetV.values[i] > 0.5) {
                    grad[i] = outputV.values[i] > 0? 1 / outputV.values[i] : MAXENTGRADIENT;
                } else {
                    grad[i] = outputV.values[i] < 1? -1 / (1 - outputV.values[i]): -MAXENTGRADIENT;
                }
            }
            return new VectorValue(grad);
        }
    }

    @Override
    public ErrorFcn getSingleton() {
        return singleton;
    }
}