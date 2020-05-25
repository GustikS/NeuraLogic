package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.learning.results.ClassificationResults;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.JavaExporter;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelLoading {
    private static final Logger LOG = Logger.getLogger(ModelLoading.class.getName());

    static String dataset = "../Resources/datasets/relational/molecules/e_coli";
    static String savedModel = "../Resources/datasets/testing/trainedTemplate_aucpr46.java";

    @TestAnnotations.Slow
    void createModel() throws Exception {
        Settings settings = Settings.forSlowTest();

//        settings.appLimitSamples = 500;

        settings.plotProgress = 10;

        settings.trainValidationPercentage = 1.0;

        settings.ruleNeuronActivation = Settings.ActivationFcn.TANH;
        settings.atomNeuronActivation = Settings.ActivationFcn.TANH;

        settings.initLearningRate = 0.001;
        settings.maxCumEpochCount = 90;
        settings.isoValueCompression = false;
        settings.chainPruning = true;
        String args = "-e " + dataset + "/trainExamples.txt -q " + dataset + "/trainQueries.txt -t " + dataset + "/templates/template_gnn_simple.txt";
        args += " -te " + dataset + "/trainExamples.txt -tq " + dataset + "/trainQueries.txt";
        Pair<Pipeline, ?> results = Main.main(Utilities.splitArgs(args), settings);
    }

    @TestAnnotations.Medium
    void loadModel() {
        Template tempalte = new JavaExporter().importObjectFrom(Paths.get(savedModel), Template.class);
        LOG.fine(Arrays.deepToString(tempalte.rules.stream().map(r -> r.getOriginalString() + "\n").collect(Collectors.toList()).toArray()));
    }

    @TestAnnotations.Medium
    void testModel() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.trainValidationPercentage = 1.0;

        settings.ruleNeuronActivation = Settings.ActivationFcn.TANH;
        settings.atomNeuronActivation = Settings.ActivationFcn.TANH;

        settings.isoValueCompression = false;
        settings.chainPruning = true;
        String args = "-te " + dataset + "/trainExamples.txt -tq " + dataset + "/trainQueries.txt -t " + savedModel;
        Pair<Pipeline, ?> results = Main.main(Utilities.splitArgs(args), settings);
    }

    @TestAnnotations.Slow
    void createLoadTestModel() throws Exception {
        Settings settings = Settings.forSlowTest();

//        settings.appLimitSamples = 10;

        settings.plotProgress = 10;

        settings.seed = 1;

        settings.trainValidationPercentage = 1.0;

        settings.ruleNeuronActivation = Settings.ActivationFcn.TANH;
        settings.atomNeuronActivation = Settings.ActivationFcn.TANH;

        settings.initLearningRate = 0.001;
        settings.maxCumEpochCount = 100;
        settings.isoValueCompression = true;
        settings.chainPruning = true;
        settings.stratification = false;

        settings.drawing = true;

        String args = "-e " + dataset + "/trainExamples.txt -q " + dataset + "/trainQueries.txt -t " + dataset + "/templates/template_gnn_simple.txt";
        args += " -te " + dataset + "/trainExamples.txt -tq " + dataset + "/trainQueries.txt";
        Pair<Pipeline, ?> results = Main.main(Utilities.splitArgs(args), settings);

        Path exportedModel = Paths.get(Settings.logFile, "export/models/trainedTemplate0.java");

        String args2 = "-te " + dataset + "/trainExamples.txt -tq " + dataset + "/trainQueries.txt -t " + exportedModel;
        Pair<Pipeline, ?> results2 = Main.main(Utilities.splitArgs(args2), settings);

        LOG.warning(results.toString());
        LOG.warning(results2.toString());
        assertEquals((ClassificationResults) results.s, (ClassificationResults) results2.s);
    }
}
