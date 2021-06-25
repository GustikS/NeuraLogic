package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
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

public class ErrorFunctions {
    private static final Logger LOG = Logger.getLogger(ErrorFunctions.class.getName());

    @TestAnnotations.Fast
    public void baseCrossentropy() throws Exception {
        String[] dataset = getDatasetArgs("neural/xor/vectorized");

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 1000;

        settings.shuffleBeforeTraining = false;
        settings.shuffleEachEpoch = false;

        settings.inferOutputNeuronFcn = true;
        settings.squishLastLayer = false;
        settings.errorFunction = Settings.ErrorFcn.CROSSENTROPY;

        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        DetailedClassificationResults classificationResults = (DetailedClassificationResults) results.s;
        Double error = ((ScalarValue) classificationResults.error).value;
        assertEquals(0.4913825870258262, error,0.000000000001);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: " + timeTaken);
        Duration limit = Duration.ofSeconds(4);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }

    @TestAnnotations.Fast
    public void squishedCrossentropyRemains() throws Exception {
        String[] dataset = getDatasetArgs("neural/xor/vectorized");

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 1000;

        settings.shuffleBeforeTraining = false;
        settings.shuffleEachEpoch = false;

        settings.inferOutputNeuronFcn = true;
        settings.squishLastLayer = true;
//        settings.errorFunction = Settings.ErrorFcn.CROSSENTROPY;

        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        DetailedClassificationResults classificationResults = (DetailedClassificationResults) results.s;
        Double error = ((ScalarValue) classificationResults.error).value;
        assertEquals(0.4913825870258262, error,0.000000000001);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: " + timeTaken);
        Duration limit = Duration.ofSeconds(4);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }
}
