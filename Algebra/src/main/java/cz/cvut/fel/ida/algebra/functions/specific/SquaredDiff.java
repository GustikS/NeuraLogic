package cz.cvut.fel.ida.algebra.functions.specific;

import cz.cvut.fel.ida.algebra.functions.ErrorFcn;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

public class SquaredDiff implements ErrorFcn {
    private static final Logger LOG = Logger.getLogger(SquaredDiff.class.getName());

    static Value oneHalf = new ScalarValue(0.5);

    public static SquaredDiff singleton = new SquaredDiff();

    @Override
    public Value evaluate(Value output, Value target) {
        if (output.getClass() != target.getClass()){
            LOG.severe("Prediction output and target label are of different algebraic types! (e.g. scalar vs vector)");
        }
        Value diff = output.minus(target);
        Value times = diff.times(diff.transpose());
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
