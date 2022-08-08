package cz.cvut.fel.ida.learning.results;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.aggregation.Average;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.results.metrics.HITS;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.*;
import java.util.logging.Logger;

/**
 * Object carrying target values with outputs, and corresponding evaluation computations.
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class Results implements Exportable<Results> {
    private static final Logger LOG = Logger.getLogger(Results.class.getName());

    transient Settings settings;

    @Deprecated
    public boolean evaluatedOnline = true;

    public transient List<Result> evaluations;

    /**
     * How to aggregate individual errors of samples. E.g. mean for MSE, or sum for SSE.
     */
    Aggregation aggregationFcn;

    /**
     * The error fcn value as measured by the respective settingsFcn over individual sample errorFcns
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
        return aggregationFcn.toString() + "-error= " + error.toDetailedString();
    }

    public abstract boolean recalculate();

    public abstract boolean betterThan(Results other, Settings.ModelSelection criterion);

    protected Results(Value meanError) {
        this.error = meanError;
    }

    public StringBuilder printOutputs(boolean sortByIndex) {
        if (sortByIndex) {
            evaluations.sort(new Comparator<Result>() {
                @Override
                public int compare(Result o1, Result o2) {
                    return Integer.compare(o1.position, o2.position);
                }
            });
        } else {
            Collections.sort(evaluations);  //sort by output (default comparator)
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Result evaluation : evaluations) {
            sb.append(evaluation.sampleId);
            sb.append(" , output: " + evaluation.getOutput().toDetailedString());
            if (evaluation.getTarget() != null) {
                sb.append(" , target: " + evaluation.getTarget());
            }
            sb.append("\n");
        }
        return sb;
    }

    private static Aggregation getAggregation(Settings settings) {
        if (settings.errorAggregationFcn == Settings.CombinationFcn.AVG) {
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

    public static abstract class Factory<R extends Results> {

        Settings settings;

        public Factory(Settings settings) {
            this.settings = settings;
        }

        public static Factory getFrom(Settings.ResultsType type, Settings settings) {
            switch (type) {
                case KBC:
                    return new KBCFactory(settings);
                case REGRESSION:
                    return new RegressionFactory(settings);
                case CLASSIFICATION:
                    return new ClassificationFactory(settings);
                case DETAILEDCLASSIFICATION:
                    return new DetailedClassificationFactory(settings);
                default:
                    throw new RuntimeException("Unknown ResultsType required");
            }
        }

        public abstract R createFrom(List<Result> outputs);

        /**
         * Possibly store some precalculated structures from the results
         *
         * @param trainingResults
         */
        public void cacheForReuse(R trainingResults) {

        }
    }

    private static class VoidFactory extends Factory<Results> {

        public VoidFactory(Settings settings) {
            super(settings);
        }

        @Override
        public Results createFrom(List<Result> outputs) {
            return new VoidResults(outputs, settings);
        }
    }

    private static class RegressionFactory extends Factory<RegressionResults> {

        public RegressionFactory(Settings settings) {
            super(settings);
        }

        @Override
        public RegressionResults createFrom(List<Result> outputs) {
            return new RegressionResults(outputs, settings);
        }
    }

    private static class ClassificationFactory extends Factory<ClassificationResults> {

        public ClassificationFactory(Settings settings) {
            super(settings);
        }

        @Override
        public ClassificationResults createFrom(List<Result> outputs) {
            return new ClassificationResults(outputs, settings);
        }
    }

    private static class DetailedClassificationFactory extends Factory<DetailedClassificationResults> {

        public DetailedClassificationFactory(Settings settings) {
            super(settings);
        }

        @Override
        public DetailedClassificationResults createFrom(List<Result> outputs) {
            return new DetailedClassificationResults(outputs, settings);
        }
    }

    private static class KBCFactory extends Factory<KBCResults> {

        HITS hits;
        private Set<HITS> consumed;

        public KBCFactory(Settings settings) {
            super(settings);
            consumed = new HashSet<>();
        }

        @Override
        public KBCResults createFrom(List<Result> outputs) {
            KBCResults kbcResults = new KBCResults(outputs, settings, hits);
            cacheForReuse(kbcResults);
            return kbcResults;
        }

        @Override
        public void cacheForReuse(KBCResults results) {
            if (hits == null) {
                hits = results.hits;
                consumed.add(results.hits);
            } else {
                if (hits != results.hits && !consumed.contains(results.hits)) {
                    hits.mergeWith(results.hits);
                    consumed.add(results.hits);
                }
            }
        }
    }
}