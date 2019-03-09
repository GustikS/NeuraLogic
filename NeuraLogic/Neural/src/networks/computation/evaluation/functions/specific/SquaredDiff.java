package networks.computation.evaluation.functions.specific;

import networks.computation.evaluation.functions.ErrorFcn;
import networks.computation.evaluation.values.Value;

import java.util.logging.Logger;

public class SquaredDiff implements ErrorFcn {
    private static final Logger LOG = Logger.getLogger(SquaredDiff.class.getName());

    @Override
    public Value evaluate(Value output, Value target) {
        Value diff = output.minus(target);
        return diff.times(diff);
    }

    @Override
    public Value differentiate(Value output, Value target) {
        Value diff = output.minus(target);
        return diff.times(diff);
    }
}
