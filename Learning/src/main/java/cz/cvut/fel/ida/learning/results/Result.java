package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.algebra.functions.ErrorFcn;
import cz.cvut.fel.ida.algebra.functions.error.Crossentropy;
import cz.cvut.fel.ida.algebra.functions.error.SoftEntropy;
import cz.cvut.fel.ida.algebra.functions.error.SquaredDiff;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
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

    public String sampleId;
    int position;

    private Value output;
    private Value target;

    /**
     * Weight of the query this result came from. Both the error and its gradient carry it, so that what gets
     * reported is the quantity training actually minimises - weighting only the gradient would optimise
     * sum(importance * error) while reporting sum(error), and the reported loss is what early stopping and
     * model selection look at.
     */
    private final double importance;

    private Result(ErrorFcn errorFcn, String sampleId, int position, Value target, Value output, double importance) {
        this.errorFcn = errorFcn;
        this.sampleId = sampleId;
        this.position = position;
        this.importance = importance;
        this.setTarget(target);
        this.setOutput(output);
    }

    /**
     * This query's share of a MEAN reduction's divisor: the number of elements in its target. Summed over a
     * batch that is torch's N x C, generalised only in that the widths may differ between queries.
     * <p>
     * Deliberately *not* weighted by importance, which was the first guess and is not what torch does.
     * **Measured**: under `reduction='mean'` torch divides by the element count and not by the sum of the
     * weights, so halving a weight halves both the loss and the gradient rather than cancelling - true of a
     * hand-rolled weighted MSE and of the built-in `BCELoss(weight=...)` alike. Putting importance in the
     * divisor would make it purely relative, and a single-query batch would ignore it entirely.
     */
    public double reductionScale() {
        int elements = 0;
        for (double ignored : getTarget()) {
            elements++;
        }
        return elements;
    }

    public Value errorValue() {
        return weighted(errorFcn.evaluate(getOutput(), getTarget()));
    }

    public Value errorGradient() {
        return weighted(errorFcn.differentiate(getOutput(), getTarget()));
    }

    private Value weighted(Value value) {
        return importance == 1.0 ? value : value.times(new ScalarValue(importance));
    }

    public double getImportance() {
        return importance;
    }

    public Value getOutput() {
        return output;
    }

    public void setOutput(Value output) {
        if (output.isNaN()) {
            throw new RuntimeException("NaN value encountered as an output result from sample " + sampleId + " - check for value/gradient exploding problems (or decrease learning rate)");
        }
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

        public Result create(String sampleId, int index, Value target, Value output) {
            return create(sampleId, index, target, output, 1.0);
        }

        public Result create(String sampleId, int index, Value target, Value output, double importance) {
            Result result = new Result(errorFcn, sampleId, index, target, output, importance);
            return result;
        }

        private ErrorFcn getErrFcn(Settings settings) {
            if (settings.errorFunction == Settings.ErrorFcn.SQUARED_DIFF)
                return SquaredDiff.singleton;
            else if (settings.errorFunction == Settings.ErrorFcn.CROSSENTROPY) {
                return Crossentropy.singleton;
            } else if (settings.errorFunction == Settings.ErrorFcn.SOFTENTROPY) {
                return SoftEntropy.singleton;
            } else return null;
            // move all getters for the enum types into Settings?
            // -> no, that would introduce unwanted dependencies in the Setting class
        }
    }

    @Override
    public String toString() {
        return sampleId + " -> " + getOutput().toDetailedString() + " : " + getTarget();
    }

    @Override
    public int compareTo(Result other) {
        return getOutput().compareTo(other.getOutput());
    }
}
