package cz.cvut.fel.ida.algebra.functions.error;

import cz.cvut.fel.ida.algebra.functions.ErrorFcn;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.logging.Logger;

public class SquaredDiff implements ErrorFcn {
    private static final Logger LOG = Logger.getLogger(SquaredDiff.class.getName());

    static Value oneHalf = new ScalarValue(0.5);
    static Value two = new ScalarValue(2);

    public static SquaredDiff singleton = new SquaredDiff();

    @Override
    public Value evaluate(Value output, Value target) {
        if (output.getClass() != target.getClass()){
            LOG.severe("Prediction output and target label are of different algebraic types! (e.g. scalar vs vector)");
        }
        Value diff = output.minus(target);

        double accumulator = 0d;

        for (double value : diff) {
            accumulator += value * value;
        }

        // Summed, not averaged over the components. `differentiate` below returns the derivative of the sum -
        // it does not divide by the element count - so dividing here reported a number that was not the
        // function being descended, by exactly the output width. For a scalar output the two are the same,
        // which is why it went unseen; for a vector target the reported error was the mean while the gradient
        // was the sum's. Crossentropy and SoftEntropy both already sum over components, so this is also what
        // makes the three agree with each other.
        return new ScalarValue(accumulator);
    }

    @Override
    public Value differentiate(Value output, Value target)   {
        return target.minus(output).times(two);
    }

    @Override
    public SquaredDiff getSingleton() {
        return singleton;
    }
}
