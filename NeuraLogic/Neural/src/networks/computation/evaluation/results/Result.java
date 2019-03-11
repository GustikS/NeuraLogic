package networks.computation.evaluation.results;

import networks.computation.evaluation.functions.ErrorFcn;
import networks.computation.evaluation.functions.specific.SquaredDiff;
import networks.computation.evaluation.values.Value;
import settings.Settings;

import java.util.logging.Logger;

/**
 * The particular result on a single sample.
 * This pair of Values deserves own class with properly named fields to prevent confusion.
 */
public class Result {
    private static final Logger LOG = Logger.getLogger(Result.class.getName());

    /**
     * How to process individual sample (output, target) into an error value. E.g. square of diff for squared error.
     */
    ErrorFcn errorFcn;

    String sampleId;

    Value output;
    Value target;

    private Result(ErrorFcn errorFcn, String sampleId, Value target, Value output) {
        this.errorFcn = errorFcn;
        this.sampleId = sampleId;
        this.target = target;
        this.output = output;
    }

    public Value errorValue() {
        return errorFcn.evaluate(output, target);
    }

    public Value errorGradient() {
        return errorFcn.differentiate(output, target);
    }

    public static class Factory {
        Settings settings;

        /**
         * How to process individual sample (output, target) into an error value. E.g. square of diff for squared error.
         */
        ErrorFcn errorFcn;

        public Factory(Settings settings) {
            this.settings = settings;
            errorFcn = getErrFcn(settings);
        }

        public Result create(String sampleId, Value target, Value output) {
            Result result = new Result(errorFcn, sampleId, target, output);
            return result;
        }

        private ErrorFcn getErrFcn(Settings settings) {
            if (settings.errorFunction == Settings.ErrorFcn.SQUARED_DIFF)
                return new SquaredDiff();
            else return null; //todo move all getters for the enum types into Settings?
        }
    }

    @Override
    public String toString() {
        return sampleId + " -> " + output + " : " + target;
    }
}
