package networks.computation.evaluation.results;

import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.functions.Average;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Object carying target values with outputs, and corresponding evaluation computations.
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
    Activation aggregationFcn;

    public Results() {
        evaluations = new ArrayList<>();
    }

    public Results(List<Result> evaluations) {
        this.evaluations = evaluations;
        this.recalculate();
    }

    public void addResult(Result result) {
        evaluations.add(result);
    }

    public abstract String toString();

    public abstract boolean recalculate();

    public abstract boolean betterThan(Results other);

    public static abstract class Factory {

        Activation aggregation;

        public Factory(Activation aggregation){
            this.aggregation = aggregation;
        }

        public static Factory getFrom(Settings settings) {
            Activation aggregationFunction = getAggregation(settings);
            if (settings.regression) {
                return new RegressionFactory(aggregationFunction);
            } else {
                if (settings.detailedResults)
                    return new DetailedClassificationFactory(aggregationFunction);
                else
                    return new ClassificationFactory(aggregationFunction);
            }
        }

        private static Activation getAggregation(Settings settings) {
            if (settings.errorAggregationFcn == Settings.AggregationFcn.AVG){
                return new Average();
            } else {
                LOG.severe("Unsupported errorAggregationFcn.");
            }
        }

        public abstract Results createFrom(List<Result> outputs);
    }

    private static class RegressionFactory extends Factory {

        public RegressionFactory(Activation aggregation) {
            super(aggregation);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            return new RegressionResults(outputs);
        }
    }

    private static class DetailedClassificationFactory extends Factory {

        public DetailedClassificationFactory(Activation aggregation) {
            super(aggregation);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            return new DetailedClassificationResults(outputs);
        }
    }

    private static class ClassificationFactory extends Factory {

        public ClassificationFactory(Activation aggregation) {
            super(aggregation);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            return new ClassificationResults(outputs);
        }
    }
}