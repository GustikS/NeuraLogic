package networks.computation.evaluation.results;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.functions.specific.Average;
import networks.computation.evaluation.values.Value;
import org.jetbrains.annotations.NotNull;
import settings.Settings;
import utils.exporting.Exportable;

import java.util.List;
import java.util.logging.Logger;

/**
 * Object carrying target values with outputs, and corresponding evaluation computations.
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class Results implements Exportable<Results> {
    private static final Logger LOG = Logger.getLogger(Results.class.getName());

    Settings settings;

    @Deprecated
    public boolean evaluatedOnline = true;

    public List<Result> evaluations;

    /**
     * How to aggregate individual errors of samples. E.g. mean for MSE, or sum for SSE.
     */
    Aggregation aggregationFcn;

    /**
     * The error fcn value as measured by the respective aggregationFcn over individual sample errorFcns
     */
    public Value error;

    public Results(@NotNull List<Result> evaluations, Settings settings) {
        this.settings = settings;
        this.evaluations = evaluations;
        this.aggregationFcn = getAggregation(settings);
        if (!evaluations.isEmpty())
            this.recalculate();
    }

    public void addResult(Result result) {
        evaluations.add(result);
    }

    @Override
    public String toString() {
        return aggregationFcn.toString() + "-error= " + error.toString();
    }

    public abstract boolean recalculate();

    public abstract boolean betterThan(Results other);

    public void printOutputs() {
        for (Result evaluation : evaluations) {
            LOG.finer(evaluation.sampleId + " : target: " + evaluation.target + " output: " + evaluation.output);
        }
    }

    private static Aggregation getAggregation(Settings settings) {
        if (settings.errorAggregationFcn == Settings.AggregationFcn.AVG) {
            return new Average();
        } else {
            LOG.severe("Unsupported errorAggregationFcn.");
        }
        return null;
    }

    public abstract String toString(Settings settings);

    public boolean isEmpty() {
        return evaluations.isEmpty();
    }

    public static abstract class Factory {

        Settings settings;

        public Factory(Settings settings) {
            this.settings = settings;
        }

        public static Factory getFrom(Settings settings) {
            if (settings.regression) {
                return new RegressionFactory(settings);
            } else {
                if (settings.detailedResults)
                    return new DetailedClassificationFactory(settings);
                else
                    return new ClassificationFactory(settings);
            }
        }

        public abstract Results createFrom(List<Result> outputs);
    }

    private static class RegressionFactory extends Factory {

        public RegressionFactory(Settings aggregation) {
            super(aggregation);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            RegressionResults regressionResults = new RegressionResults(outputs, settings);
            return regressionResults;
        }
    }

    private static class DetailedClassificationFactory extends Factory {

        public DetailedClassificationFactory(Settings aggregation) {
            super(aggregation);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            DetailedClassificationResults detailedClassificationResults = new DetailedClassificationResults(outputs, settings);
            return detailedClassificationResults;
        }
    }

    private static class ClassificationFactory extends Factory {

        public ClassificationFactory(Settings aggregation) {
            super(aggregation);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            ClassificationResults classificationResults = new ClassificationResults(outputs, settings);
            return classificationResults;
        }
    }
}