package cz.cvut.fel.ida.neuralogic.cli.datasets.neural;

import cz.cvut.fel.ida.learning.results.DetailedClassificationResults;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.time.Duration;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XOR {
    private static final Logger LOG = Logger.getLogger(XOR.class.getName());

    @TestAnnotations.Fast
    public void naive() throws Exception {
        String[] dataset = getDatasetArgs("neural/xor/naive");

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 1000;
        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        DetailedClassificationResults classificationResults = (DetailedClassificationResults) results.s;
        Double bestAccuracy = classificationResults.bestAccuracy;
        assertEquals(bestAccuracy, 1.0);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: " + timeTaken);
        Duration limit = Duration.ofSeconds(4);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }

    @TestAnnotations.Fast
    public void vectorized() throws Exception {
        String[] dataset = getDatasetArgs("neural/xor/vectorized");

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 1000;

        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        DetailedClassificationResults classificationResults = (DetailedClassificationResults) results.s;
        Double bestAccuracy = classificationResults.bestAccuracy;
        assertEquals(bestAccuracy, 1.0);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: " + timeTaken);
        Duration limit = Duration.ofSeconds(4);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }

    @TestAnnotations.Fast
    public void solution() throws Exception {
        String[] dataset = getDatasetArgs("neural/xor/solution");

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        DetailedClassificationResults classificationResults = (DetailedClassificationResults) results.s;
        Double bestAccuracy = classificationResults.accuracy;
        assertEquals(bestAccuracy, 1.0);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: " + timeTaken);
        Duration limit = Duration.ofMillis(1500);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }

    @TestAnnotations.Interactive
    public void drawing() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        settings.maxCumEpochCount = 5;
        settings.isoValueCompression = false;
        settings.chainPruning = false;
        settings.resultsRecalculationEpochae = 5;
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs("neural/xor/naive", "-debug all"), settings);
    }

    @TestAnnotations.Interactive
    public void drawingVector() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        settings.maxCumEpochCount = 5;
        settings.isoValueCompression = false;
        settings.chainPruning = false;
        settings.resultsRecalculationEpochae = 5;
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs("neural/xor/vectorized", "-debug all"), settings);
    }
}