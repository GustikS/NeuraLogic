package networks.computation.evaluation.functions.specific;

import networks.computation.evaluation.functions.ErrorFcn;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;

import java.util.logging.Logger;

public class SquaredDiff implements ErrorFcn {
    private static final Logger LOG = Logger.getLogger(SquaredDiff.class.getName());

    Value oneHalf = new ScalarValue(0.5);

    @Override
    public Value evaluate(Value output, Value target) {
        Value diff = output.minus(target);
        Value times = diff.times(diff);
        return times.times(oneHalf);
    }

    @Override
    public Value differentiate(Value output, Value target) {
        return output.minus(target);
    }
}
