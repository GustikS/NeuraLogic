package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

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

import static cz.cvut.fel.ida.utils.generic.Utilities.splitArgs;

public class Mutagenesis {
    private static final Logger LOG = Logger.getLogger(Mutagenesis.class.getName());


    static String dataset = "relational/molecules/mutagenesis";
    String[] args = Utilities.getDatasetArgs(dataset, "");

    /**
     * For mutagenesis better keep lr=0.3 for backward compatibility
     * @throws Exception
     */
    @TestAnnotations.Slow
    public void defaultMutagenPerformanceSGD() throws Exception {
        double referenceDispersion = 0.6109891807533523;
        Duration referenceTime = Duration.ofMinutes(9);

        Settings settings = Settings.forSlowTest();
        settings.seed = 0;
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.initLearningRate = 0.3;

        settings.resultsRecalculationEpochae = 10;
        settings.trainValidationPercentage = 1.0;

        settings.plotProgress = 10;

        settings.isoValueCompression = true;
        settings.chainPruning = true;
        settings.maxCumEpochCount = 1000;

        Pair<Pipeline, ?> results = Main.main(args, settings);
        Benchmarking.assertDispersionAndTime(WorkflowUtils.getDisperionAndTime(results), referenceDispersion, referenceTime);
    }

    /**
     * For fast and superior result we need to increase learning rate for ADAM (e.g. to 0.01) to beat SGD with 0.3
     * @throws Exception
     */
    @TestAnnotations.Slow
    public void defaultMutagenPerformanceADAM() throws Exception {
        double referenceDispersion = 0.7510077938552104;
        Duration referenceTime = Duration.ofMinutes(9);

        Settings settings = Settings.forSlowTest();
        settings.seed = 0;
        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.01;

        settings.resultsRecalculationEpochae = 10;
        settings.trainValidationPercentage = 1.0;

        settings.plotProgress = 10;

        settings.isoValueCompression = true;
        settings.chainPruning = true;
        settings.maxCumEpochCount = 1000;

        settings.parallelGrounding = true;

        Pair<Pipeline, ?> results = Main.main(args, settings);
        Benchmarking.assertDispersionAndTime(WorkflowUtils.getDisperionAndTime(results), referenceDispersion, referenceTime);
    }

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
//        settings.trainValidationPercentage = 1.0;

        settings.plotProgress = 10;

        settings.crossvalidation = true;

        settings.isoValueCompression = true;
        settings.chainPruning = true;
        settings.maxCumEpochCount = 1000;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_gnnW10.txt"), settings);
        Benchmarking.assertDispersionAndTime(WorkflowUtils.getDisperionAndTime(results), referenceDispersion, referenceTime);
    }


    /**
     * For mutagenesis better keep lr=0.3 for backward compatibility
     * @throws Exception
     */
    @TestAnnotations.Fast
    public void mutagenImportTemplate() throws Exception {

        Settings settings = Settings.forFastTest();

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_gnn.txt"), settings);
    }

    @TestAnnotations.AdHoc
    public void mergedExamples() throws Exception {
        Settings settings = Settings.forSlowTest();
        Pair<Pipeline, ?> results = Main.main(splitArgs("-e ../Resources/datasets/relational/molecules/mutagenesis/examples_merged.txt -q ../Resources/datasets/relational/molecules/mutagenesis/query_merged.txt  -t ../Resources/datasets/relational/molecules/mutagenesis/templates/template_gnn.txt"), settings);
    }

    @TestAnnotations.AdHoc
    public void noBonds() throws Exception {
        Settings settings = Settings.forSlowTest();
//        settings.isoValueCompression = false;
        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutagenesis", "-t ./templates/template_nobonds"), settings);
    }
}