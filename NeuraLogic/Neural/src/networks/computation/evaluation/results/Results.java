package networks.computation.evaluation.results;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.functions.specific.Average;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Object carrying target values with outputs, and corresponding evaluation computations.
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class Results {
    private static final Logger LOG = Logger.getLogger(Results.class.getName());

    @Deprecated
    public boolean evaluatedOnline = true;

    List<Result> evaluations;

    /**
     * How to aggregate individual errors of samples. E.g. mean for MSE, or sum for SSE.
     */
    Aggregation aggregationFcn;

    public Results() {
        evaluations = new ArrayList<>();
    }

    public Results(List<Result> evaluations, Aggregation aggregationFcn) {
        this.evaluations = evaluations;
        this.aggregationFcn = aggregationFcn;
        if (!evaluations.isEmpty())
            this.recalculate();
    }

    public void addResult(Result result) {
        evaluations.add(result);
    }

    public abstract String toString();

    public abstract boolean recalculate();

    public abstract boolean betterThan(Results other);

    public static abstract class Factory {

        Aggregation aggregation;

        public Factory(Aggregation aggregation) {
            this.aggregation = aggregation;
        }

        public static Factory getFrom(Settings settings) {
            Aggregation aggregationFunction = getAggregation(settings);
            if (settings.regression) {
                return new RegressionFactory(aggregationFunction);
            } else {
                if (settings.detailedResults)
                    return new DetailedClassificationFactory(aggregationFunction);
                else
                    return new ClassificationFactory(aggregationFunction);
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

        public abstract Results createFrom(List<Result> outputs);
    }

    private static class RegressionFactory extends Factory {

        public RegressionFactory(Aggregation aggregation) {
            super(aggregation);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            RegressionResults regressionResults = new RegressionResults(outputs, aggregation);
            return regressionResults;
        }
    }

    private static class DetailedClassificationFactory extends Factory {

        public DetailedClassificationFactory(Aggregation aggregation) {
            super(aggregation);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            DetailedClassificationResults detailedClassificationResults = new DetailedClassificationResults(outputs, aggregation);
            return detailedClassificationResults;
        }
    }

    private static class ClassificationFactory extends Factory {

        public ClassificationFactory(Aggregation aggregation) {
            super(aggregation);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            ClassificationResults classificationResults = new ClassificationResults(outputs, aggregation);
            return classificationResults;
        }
    }
}