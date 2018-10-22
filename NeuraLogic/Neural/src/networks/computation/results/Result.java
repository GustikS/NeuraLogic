package networks.computation.results;

import networks.computation.training.evaluation.values.Value;

import java.util.logging.Logger;

/**
 * This pair of Values deserves own class with properly named fields to prevent confusion.
 */
public class Result {
    private static final Logger LOG = Logger.getLogger(Result.class.getName());

    Value output;
    Value target;

    public Result(Value target, Value output) {
        this.target = target;
        this.output = output;
    }
}
