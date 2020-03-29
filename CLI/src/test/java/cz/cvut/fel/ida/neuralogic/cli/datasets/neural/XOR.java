package cz.cvut.fel.ida.neuralogic.cli.datasets.neural;

import cz.cvut.fel.ida.learning.results.DetailedClassificationResults;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XOR {
    private static final Logger LOG = Logger.getLogger(XOR.class.getName());

    public static String[] dataset = getDatasetArgs("neural/xor/vectorized");

    @Test
    public void vectorized() throws Exception {
        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 1000;
        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        DetailedClassificationResults classificationResults = (DetailedClassificationResults) results.s;
        Double bestAccuracy = classificationResults.bestAccuracy;
        assertEquals(bestAccuracy, 1.0);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: "  + timeTaken);
        Duration limit = Duration.ofSeconds(3);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }
}