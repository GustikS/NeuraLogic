package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.learning.results.ClassificationResults;
import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelLoading {
    private static final Logger LOG = Logger.getLogger(ModelLoading.class.getName());

    static String dataset = "../Resources/datasets/relational/molecules/mutagenesis";

    @TestAnnotations.Medium
    void trainTestConsistency() throws Exception {
        Settings settings = Settings.forSlowTest();

//        settings.plotProgress = 10;

        settings.trainValidationPercentage = 1.0;

        settings.ruleNeuronTransformation = Settings.TransformationFcn.TANH;
        settings.atomNeuronTransformation = Settings.TransformationFcn.TANH;

        settings.initLearningRate = 0.001;
        settings.maxCumEpochCount = 90;
        settings.isoValueCompression = false;
        settings.chainPruning = true;
        String args = "-e " + dataset + "/splits/trainExamples.txt -t " + dataset + "/templates/template_gnn.txt";
        Pair<Pipeline, ?> trainResults = Main.main(Utilities.splitArgs(args), settings);
        Double trainDispersion = ((ClassificationResults) trainResults.s).dispersion;

        settings.inferred.maxWeightCount = new AtomicInteger(0);
        args += " -te " + dataset + "/splits/trainExamples.txt";
        Pair<Pipeline, ?> testResults = Main.main(Utilities.splitArgs(args), settings);
        Double testDispersion = ((ClassificationResults) testResults.s).dispersion;
        assertEquals(trainDispersion, testDispersion, 0.000000000001);
    }

    /**
     * Correct model storing+loading, checked via giving the same results on the same sample set.
     * This is an important test.
     *
     * @throws Exception
     */
    @TestAnnotations.Medium
    void createLoadTestModel() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.trainValidationPercentage = 1.0;

        settings.ruleNeuronTransformation = Settings.TransformationFcn.TANH;
        settings.atomNeuronTransformation = Settings.TransformationFcn.TANH;

        settings.initLearningRate = 0.001;

        settings.maxCumEpochCount = 100;

        settings.isoValueCompression = true;
        settings.chainPruning = true;
        settings.stratification = false;

        settings.drawing = true;

        String args = "-e " + dataset + "/splits/trainExamples.txt -t " + dataset + "/templates/template_gnn.txt";
        args += " -te " + dataset + "/splits/trainExamples.txt";
        Pair<Pipeline, ?> results = Main.main(Utilities.splitArgs(args), settings);

        File exportedModel = Paths.get(Logging.logFile.toString(), "export/models/").toFile().listFiles()[0];
        String args2 = "-te " + dataset + "/splits/trainExamples.txt -t " + exportedModel;
        Pair<Pipeline, ?> results2 = Main.main(Utilities.splitArgs(args2), settings);

        LOG.warning(results.toString());
        LOG.warning(results2.toString());
        assertEquals(((ClassificationResults) results.s).dispersion, ((ClassificationResults) results2.s).dispersion, 0.0000000000001);
    }
}
