package cz.cvut.fel.ida.pipelines.utils;

import cz.cvut.fel.ida.learning.results.ClassificationResults;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.time.Duration;
import java.util.logging.Logger;

public class WorkflowUtils {
    private static final Logger LOG = Logger.getLogger(WorkflowUtils.class.getName());

    public static Pair<Double, Duration> getDisperionAndTime(Pair<Pipeline, ?> results) {
        ClassificationResults classificationResults = (ClassificationResults) results.s;
        Double dispersion = classificationResults.dispersion;
        Duration timeTaken = results.r.timing.getTimeTaken();
        return new Pair<>(dispersion, timeTaken);
    }
}
