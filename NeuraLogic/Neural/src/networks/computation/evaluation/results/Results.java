package networks.computation.evaluation.results;

import networks.computation.evaluation.functions.Activation;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Object carying target values with outputs, and corresponding evaluation computations.
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class Results {
    List<Result> outputs;

    /**
     * How to aggregate individual errors of samples. E.g. mean for MSE, or sum for SSE.
     */
    Activation aggregationFcn;

    public Results() {
        outputs = new ArrayList<>();
    }

    public Results(List<Result> outputs) {
        this.outputs = outputs;
        this.recalculate();
    }

    public abstract String toString();

    public void addResult(Result result) {
        outputs.add(result);
    }

    public abstract boolean recalculate();

    public static Results getFrom(Settings settings, List<Result> outputs) {
        if (settings.regression) {
            return new RegressionResults(outputs);
        } else {
            if (settings.detailedResults)
                return new DetailedClassificationResults(outputs);
            else
                return new ClassificationResults(outputs);
        }
    }
}