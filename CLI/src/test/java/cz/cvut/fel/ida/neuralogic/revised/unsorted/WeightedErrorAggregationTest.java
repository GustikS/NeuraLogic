package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.results.ClassificationResults;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The per-sample errors carry their query's importance, so aggregating them has to account for it: a mean
 * divides by the total weight rather than the sample count, while a sum needs no correction.
 */
public class WeightedErrorAggregationTest {
    private static final Logger LOG = Logger.getLogger(WeightedErrorAggregationTest.class.getName());

    @TestAnnotations.Fast
    public void scalingEveryImportanceLeavesTheMeanAlone() {
        Settings settings = averaging();

        double plain = error(settings, new double[]{1.0, 1.0, 1.0}, new double[]{0.2, 0.5, 0.9});
        double scaled = error(settings, new double[]{3.0, 3.0, 3.0}, new double[]{0.2, 0.5, 0.9});

        assertEquals(plain, scaled, 1e-12, "a mean must not move when all weights are scaled alike");
    }

    /**
     * Compared against the weighted mean worked out by hand from the very same per-sample errors, so that this
     * says nothing about which error function is configured - only about how the aggregation treats weights.
     */
    @TestAnnotations.Fast
    public void theMeanDividesByTheTotalWeight() {
        Settings settings = averaging();
        double[] importances = {3.0, 1.0, 0.5};
        double[] outputs = {0.0, 1.0, 0.7};

        Result.Factory factory = new Result.Factory(settings);
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < importances.length; i++) {
            results.add(factory.create("s" + i, i, new ScalarValue(0.0), new ScalarValue(outputs[i]), importances[i]));
        }

        double weightedSum = 0;
        double totalWeight = 0;
        for (int i = 0; i < results.size(); i++) {
            weightedSum += scalar(results.get(i).errorValue());   //already carries its importance
            totalWeight += importances[i];
        }

        double aggregated = scalar(new ClassificationResults(results, settings).calculateErrorValue());
        assertEquals(weightedSum / totalWeight, aggregated, 1e-12);
    }

    @TestAnnotations.Fast
    public void summingNeedsNoCorrection() {
        Settings settings = averaging();
        settings.errorAggregationFcn = Settings.CombinationFcn.SUM;

        double single = error(settings, new double[]{1.0}, new double[]{1.0});
        double doubled = error(settings, new double[]{2.0}, new double[]{1.0});

        assertEquals(2 * single, doubled, 1e-12, "a weighted sum scales with the weight");
    }

    private static Settings averaging() {
        Settings settings = Settings.forFastTest();
        settings.errorAggregationFcn = Settings.CombinationFcn.AVG;
        settings.errorFunction = Settings.ErrorFcn.SQUARED_DIFF;
        settings.inferOutputFcns = false;
        // ClassificationResults squashes the outputs in place while computing its metrics, which would make
        // calculateErrorValue answer differently the second time around - this test is about the weights only
        settings.squishLastLayer = false;
        settings.infer();
        return settings;
    }

    private static double error(Settings settings, double[] importances, double[] outputs) {
        Result.Factory factory = new Result.Factory(settings);
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < importances.length; i++) {
            results.add(factory.create("s" + i, i, new ScalarValue(0.0), new ScalarValue(outputs[i]), importances[i]));
        }
        ClassificationResults aggregated = new ClassificationResults(results, settings);
        return scalar(aggregated.calculateErrorValue());
    }

    private static double scalar(Value value) {
        double result = 0;
        for (Double element : value) {
            result += element;
        }
        return result;
    }
}
