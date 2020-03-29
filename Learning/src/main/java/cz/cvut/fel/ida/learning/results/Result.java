package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.algebra.functions.ErrorFcn;
import cz.cvut.fel.ida.algebra.functions.specific.Crossentropy;
import cz.cvut.fel.ida.algebra.functions.specific.SquaredDiff;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

/**
 * The particular result on a single sample.
 * This pair of Values deserves own class with properly named fields to prevent confusion.
 */
public class Result implements Comparable<Result> {
    private static final Logger LOG = Logger.getLogger(Result.class.getName());

    /**
     * How to process individual sample (output, target) into an error value. E.g. square of diff for squared error.
     */
    ErrorFcn errorFcn;

    String sampleId;

    private Value output;
    private Value target;

    private Result(ErrorFcn errorFcn, String sampleId, Value target, Value output) {
        this.errorFcn = errorFcn;
        this.sampleId = sampleId;
        this.setTarget(target);
        this.setOutput(output);
    }

    public Value errorValue() {
        return errorFcn.evaluate(getOutput(), getTarget());
    }

    public Value errorGradient() {
        return errorFcn.differentiate(getOutput(), getTarget());
    }

    public Value getOutput() {
        return output;
    }

    public void setOutput(Value output) {
        this.output = output;
    }

    public Value getTarget() {
        return target;
    }

    public void setTarget(Value target) {
        this.target = target;
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
            else if (settings.errorFunction == Settings.ErrorFcn.CROSSENTROPY){
                return new Crossentropy();
            }
            else return null; //todo move all getters for the enum types into Settings?
        }
    }

    @Override
    public String toString() {
        return sampleId + " -> " + getOutput() + " : " + getTarget();
    }

    @Override
    public int compareTo(Result other) {
        return getOutput().compareTo(other.getOutput());
    }
}
