package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class EColi {
    private static final Logger LOG = Logger.getLogger(EColi.class.getName());
    static String dataset = "relational/molecules/e_coli";

    @TestAnnotations.AdHoc
    public void defaultEcoliPerformance() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
//        settings.initLearningRate = 0.3;

        settings.crossvalidation = true;

        settings.maxCumEpochCount = 1000;
        settings.plotProgress = 10;

        settings.trainValidationPercentage = 0.9;

        settings.appLimitSamples = 500;
        settings.isoValueCompression = false;
        settings.chainPruning = true;
        settings.processMetadata = true;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./templates/gnns/templateW10.txt"), settings);
    }

    @TestAnnotations.AdHoc
    public void defaultEcoliPerformanceCross() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.maxCumEpochCount = 10000;
        settings.plotProgress = 5;

//        settings.appLimitSamples = 1804;
        settings.isoValueCompression = false;
        settings.chainPruning = false;

//        settings.errorFunction = Settings.ErrorFcn.CROSSENTROPY;
//        settings.ruleNeuronActivation = Settings.ActivationFcn.TANH;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./templates/template_cross.txt"), settings);
    }



    @TestAnnotations.AdHoc
    public void defaultEcoliPerformanceGNN() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.plotProgress = 10;
        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.0001;
        settings.maxCumEpochCount = 200;

        settings.atomNeuronTransformation = Settings.TransformationFcn.TANH;
        settings.ruleNeuronTransformation = Settings.TransformationFcn.TANH;

        settings.trainValidationPercentage = 1.0;

        settings.isoValueCompression = true;
        settings.chainPruning = false;
        settings.processMetadata = true;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./templates/template_gnnW10.txt"), settings);
    }


    @TestAnnotations.AdHoc
    public void defaultEcoliPerformanceCross_08AUC() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.maxCumEpochCount = 400;
        settings.plotProgress = 60;

        settings.appLimitSamples = -1;
        settings.isoValueCompression = false;
        settings.chainPruning = false;
        settings.processMetadata = true;

        settings.trainValidationPercentage = 0.9;
        settings.errorFunction = Settings.ErrorFcn.CROSSENTROPY;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./templates/template_cross.txt"), settings);
    }

    @TestAnnotations.AdHoc
    @TestAnnotations.Parameterized
    @ValueSource(strings = {
            "./templates/gnns/templateW10.txt",
            "./templates/gnns/templateW10_l2.txt",
            "./templates/gnns/template_gnnW10_l1.txt",
            "./templates/gnns/template_gnnW10_l2.txt",
            "./templates/gnns/template_gnnW10_l3.txt",
    })
    public void testTemplates(String template) throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.maxCumEpochCount = 10;
        settings.appLimitSamples = 10;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t " + template), settings);
    }

    @TestAnnotations.AdHoc
    public void trainValTest() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;
        settings.drawing = true;
        settings.debugPipeline = true;
        settings.storeNotShow = true;
        settings.isoValueCompression = false;

//        settings.appLimitSamples = 100;

        settings.maxCumEpochCount = 10;
        settings.plotProgress = 10;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./templates/template_gnnW10.txt -te ./valExamples.txt -tq ./valQueriesNoTarget.txt"), settings);
    }
}
