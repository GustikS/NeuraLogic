package networks.computation.evaluation.results;

import networks.computation.evaluation.values.Value;
import networks.computation.evaluation.functions.Activation;

import java.util.logging.Logger;

/**
 * This pair of Values deserves own class with properly named fields to prevent confusion.
 */
public class Result {
    private static final Logger LOG = Logger.getLogger(Result.class.getName());

    Activation errorFcn; //todo here?

    Value output;
    Value target;

    public Result(Value target, Value output) {
        this.target = target;
        this.output = output;
    }
}
