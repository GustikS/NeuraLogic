package cz.cvut.fel.ida.neuralogic.cli.datasets.simple;

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

public class Horses {
    private static final Logger LOG = Logger.getLogger(Horses.class.getName());

    @TestAnnotations.Slow
    public void horsesSetting() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.maxCumEpochCount = 500;
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs("simple/family"), settings);
        DetailedClassificationResults classificationResults = (DetailedClassificationResults) results.s;
        Double bestAccuracy = classificationResults.accuracy;
        assertEquals(bestAccuracy, 1.0);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: "  + timeTaken);
        Duration limit = Duration.ofSeconds(3);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }

    @TestAnnotations.Slow
    public void horsesDrawing() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        settings.maxCumEpochCount = 5;
        settings.isoValueCompression = false;
        settings.chainPruning = false;
        settings.resultsRecalculationEpochae = 5;
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs("simple/family", "-debug all"), settings);
    }
}