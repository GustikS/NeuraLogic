package networks.computation.evaluation.functions.specific;

import networks.computation.evaluation.functions.ErrorFcn;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;

import java.util.logging.Logger;

public class Crossentropy implements ErrorFcn {
    private static final Logger LOG = Logger.getLogger(Crossentropy.class.getName());

    static ScalarValue oneHalf = new ScalarValue(0.5);
    static ScalarValue one = new ScalarValue(1);
    static ScalarValue minusOne = new ScalarValue(-1);

    static Crossentropy singleton = new Crossentropy();

    @Override
    public Value evaluate(Value output, Value target) {
        if (target.greaterThan(oneHalf)) {
            return output.apply(x -> -Math.log(x));
        } else {
            return output.apply(x -> -Math.log(1 - x));
        }
    }

    @Override
    public Value differentiate(Value output, Value target) {    //assuming 0/1 target!
        if (target.greaterThan(oneHalf)) {
            return output.apply(x -> 1 / x);
        } else {
            return output.apply(x -> -1 / (1 - x));
        }
    }

    @Override
    public ErrorFcn getSingleton() {
        return singleton;
    }
}