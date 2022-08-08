package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.utils.WorkflowUtils;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Benchmarking;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.time.Duration;
import java.util.logging.Logger;

public class trainValTest {
    private static final Logger LOG = Logger.getLogger(trainValTest.class.getName());

    @TestAnnotations.Slow
    public void defaultMutagenPerformanceADAM_unifiedTemplate() throws Exception {
        double referenceDispersion = 0.7510077938552104;
        Duration referenceTime = Duration.ofMinutes(9);

        Settings settings = Settings.forSlowTest();
        settings.seed = 0;
        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.00015;

        settings.modelSelection = Settings.ModelSelection.ERROR;

        settings.atomNeuronTransformation = Settings.TransformationFcn.TANH;
        settings.ruleNeuronTransformation = Settings.TransformationFcn.TANH;

        settings.resultsRecalculationEpochae = 10;
        settings.trainValidationPercentage = 0.8;

        settings.plotProgress = 10;

        settings.crossvalidation = false;

        settings.isoValueCompression = true;
        settings.chainPruning = true;
        settings.maxCumEpochCount = 1000;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_gnnW10.txt"), settings);
        Benchmarking.assertDispersionAndTime(WorkflowUtils.getDisperionAndTime(results), referenceDispersion, referenceTime);
    }
}
