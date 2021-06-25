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

    public static Crossentropy singleton = new Crossentropy();

    @Override
    public Value evaluate(Value output, Value target) {
        if (target instanceof ScalarValue) {    // binary crossentropy
            if (target.greaterThan(oneHalf)) {
                return output.apply(x -> -Math.log(x));
            } else {
                return output.apply(x -> -Math.log(1 - x));
            }
        } else {    // general crossentropy
            VectorValue outputV = (VectorValue) output;
            VectorValue targetV = (VectorValue) target;
            double err = 0;
            for (int i = 0; i < outputV.values.length; i++) { // full loop to account possibly even for multiple labels at the same time
                err -= targetV.values[i] * Math.log(outputV.values[i]);
            }
            return new ScalarValue(err);
        }
    }

    @Override
    public Value differentiate(Value output, Value target) {    //assuming 0/1 target!
        if (target instanceof ScalarValue) {    // binary crossentropy
            if (target.greaterThan(oneHalf)) {
                return output.apply(x -> 1 / x);
            } else {
                return output.apply(x -> -1 / (1 - x));
            }
        } else {    // general crossentropy
            VectorValue outputV = (VectorValue) output;
            VectorValue targetV = (VectorValue) target;
            double[] grad = new double[outputV.values.length];
            for (int i = 0; i < outputV.values.length; i++) {
                if (targetV.values[i] > 0.5) {
                    grad[i] = 1 / outputV.values[i];
                } else {
                    grad[i] = -1 / (1 - outputV.values[i]);
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