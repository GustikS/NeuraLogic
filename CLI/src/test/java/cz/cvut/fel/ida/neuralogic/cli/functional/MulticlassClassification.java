package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.learning.results.ClassificationResults;
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

public class MulticlassClassification {
    private static final Logger LOG = Logger.getLogger(MulticlassClassification.class.getName());

    @TestAnnotations.Fast
    public void squishLastLayer() throws Exception {
        String[] dataset = getDatasetArgs("neural/xor/multiclass");

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 1000;

        settings.inferOutputFcns = true;
        settings.squishLastLayer = true;

        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        ClassificationResults classificationResults = (ClassificationResults) results.s;
        assertEquals(classificationResults.accuracy, 1.0);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: " + timeTaken);
        Duration limit = Duration.ofSeconds(4);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }

    /**
     * There is no actual softmax here...
     * @throws Exception
     */
    @TestAnnotations.Fast
    public void unsquished() throws Exception {
        String[] dataset = getDatasetArgs("neural/xor/multiclass");

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 1000;

        settings.inferOutputFcns = true;
        settings.squishLastLayer = false;

        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        ClassificationResults classificationResults = (ClassificationResults) results.s;
        assertEquals(classificationResults.accuracy, 1.0);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: " + timeTaken);
        Duration limit = Duration.ofSeconds(4);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }

    @TestAnnotations.Fast
    public void threeClasses() throws Exception {
        String[] dataset = getDatasetArgs("neural/xor/multiclass","-t ./template3.txt","-e ./trainExamples3.txt");

        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        settings.maxCumEpochCount = 1000;

        settings.inferOutputFcns = true;
        settings.squishLastLayer = false;

        Pair<Pipeline, ?> results = Main.main(dataset, settings);
        ClassificationResults classificationResults = (ClassificationResults) results.s;
        assertEquals(classificationResults.accuracy, 1.0);
        Duration timeTaken = results.r.timing.getTimeTaken();
        LOG.warning("time taken: " + timeTaken);
        Duration limit = Duration.ofSeconds(4);
        assertTrue(timeTaken.compareTo(limit) < 0);
    }
}
