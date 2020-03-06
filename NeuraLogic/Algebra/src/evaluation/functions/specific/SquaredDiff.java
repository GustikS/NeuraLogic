package evaluation.functions.specific;

import evaluation.functions.ErrorFcn;
import evaluation.values.ScalarValue;
import evaluation.values.Value;

import java.util.logging.Logger;

public class SquaredDiff implements ErrorFcn {
    private static final Logger LOG = Logger.getLogger(SquaredDiff.class.getName());

    Value oneHalf = new ScalarValue(0.5);

    static SquaredDiff singleton = new SquaredDiff();

    @Override
    public Value evaluate(Value output, Value target) {
        Value diff = output.minus(target);
        Value times = diff.times(diff);
//        return times.times(oneHalf);  //this is technically correct, but less interpretable
        return times;
    }

    @Override
    public Value differentiate(Value output, Value target)   {
        return target.minus(output);
    }

    @Override
    public SquaredDiff getSingleton() {
        return singleton;
    }
}
