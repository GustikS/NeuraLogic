package cz.cvut.fel.ida.neuralogic.diffcheck;

import cz.cvut.fel.ida.learning.results.ClassificationResults;
import cz.cvut.fel.ida.learning.results.DetailedClassificationResults;
import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Mutagenesis {
    private static final Logger LOG = Logger.getLogger(Mutagenesis.class.getName());

    String[] args2ex = Utilities.getDatasetArgs("relational/molecules/mutagenesis/diffcheck", "-e examples2only.txt");
    String[] argsAll = Utilities.getDatasetArgs("relational/molecules/mutagenesis/diffcheck", "-q allQueries.txt");
    String[] argsVect = Utilities.getDatasetArgs("relational/molecules/mutagenesis/diffcheck", "-q ./allQueries.txt -t ./template_vectorized.txt");
    String[] argsNew = Utilities.getDatasetArgs("relational/molecules/mutagenesis/", "-t ./templates/template_older_vectorized.txt", "-q ./diffcheck/allQueries.txt");

    /**
     * DO NOT TOUCH THIS!!
     * - this test is also a bit numerically unstable
     *
     * @throws Exception
     */
    @TestAnnotations.Medium
    public void mutagen_diffcheck2examplesEXACT() throws Exception {
        Logging logging = Logging.initLogging(new Settings());

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.CONSTANT;

        // the seed 3 matches the old version with seed 1 with disp value = 0.0014794164025840328
        // - try to change seed to 0 back and forth few times if it doesnt work (also, no debug mode!)
        settings.seed = 3;  //the seed matters only from 15th decimal place further and there are only 2-3 variants (probably orderings in summations)
        settings.shuffleBeforeFoldSplit = false;
        settings.shuffleBeforeTraining = false;
        settings.shuffleEachEpoch = false;
        settings.debugSampleOutputs = true;
        settings.trainValidationPercentage = 1.0;

        settings.maxCumEpochCount = 100;
        settings.resultsRecalculationEpochae = 1;

        settings.atomNeuronTransformation = Settings.TransformationFcn.SIGMOID;
        settings.ruleNeuronTransformation = Settings.TransformationFcn.SIGMOID;

        settings.errorAggregationFcn = Settings.CombinationFcn.AVG;
        settings.errorFunction = Settings.ErrorFcn.SQUARED_DIFF;

        settings.inferOutputFcns = false;
        settings.squishLastLayer = false;

        settings.calculateBestThreshold = true;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.initLearningRate = 0.3;
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;  //this is also testing the equivalence of DFSRecursive and TOPOLOGIC

        settings.oneQueryPerExample = true;
//        settings.neuralNetsPostProcessing = true; //but only chain-pruning! this is to match the old optimized lambda-kappa template (aggregations were only in lambdas)
        settings.isoValueCompression = false;
        settings.chainPruning = true;
        settings.storeNotShow = true;

        settings.debugExporting = true;

        Pair<Pipeline, ?> main = Main.main(args2ex, settings);
        ClassificationResults results = (ClassificationResults) main.s;
        assertEquals(0.002737286270142636, results.dispersion, 0.000000000000001);
    }

    /**
     * true results :- train: accuracy: 91.49%, disp: 0.6420141023483763, error: AVG(SQUARED_DIFF) = 0.0701644803, (best thresh acc: 93.62%) (maj. 66.49%), (AUC-ROC: 0.9556825397), (AUC-PR: 0.9736792284)
     * Total time: 02:12:49:596
     *
     * @throws Exception
     */
    @TestAnnotations.Slow
    public void mutagen_diffcheck_standard() throws Exception {

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.UNIFORM;

        settings.trainValidationPercentage = 1.0;
//        settings.plotProgress = 20;

        settings.seed = 0;
        settings.maxCumEpochCount = 1000;
        settings.resultsRecalculationEpochae = 10;

        settings.atomNeuronTransformation = Settings.TransformationFcn.SIGMOID;
        settings.ruleNeuronTransformation = Settings.TransformationFcn.SIGMOID;

        settings.shuffleEachEpoch = true;
        settings.debugSampleOutputs = false;
        settings.calculateBestThreshold = true;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.initLearningRate = 0.3;
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;

        settings.errorFunction = Settings.ErrorFcn.SQUARED_DIFF;
        settings.inferOutputFcns = false;
        settings.squishLastLayer = false;

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = false;
        settings.chainPruning = true;
        settings.isoValueCompression = false;
        settings.storeNotShow = true;

        Pair<Pipeline, ?> main = Main.main(argsAll, settings);
        DetailedClassificationResults results = (DetailedClassificationResults) main.s;
        assertEquals(0.6420141023483763, results.dispersion, 0.01);
    }

    /**
     * true results :- train: accuracy: 90.43%, disp: 0.6190426133500868, error: AVG(SQUARED_DIFF) = 0.0739864, (best thresh acc: 92.02%) (maj. 66.49%), (AUC-ROC: 0.9564444444), (AUC-PR: 0.9756295178)
     * Total time: 00:36:39:985
     *
     * @throws Exception
     */
    @TestAnnotations.Slow
    public void mutagen_diffcheck_vectorized() throws Exception {

        Settings settings = new Settings();

        settings.trainValidationPercentage = 1.0;

        settings.initDistribution = Settings.InitDistribution.UNIFORM;
//        settings.plotProgress = 20;

        settings.seed = 0;
        settings.maxCumEpochCount = 1000;
        settings.resultsRecalculationEpochae = 10;
        settings.shuffleEachEpoch = true;
        settings.debugSampleOutputs = false;
        settings.calculateBestThreshold = true;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.initLearningRate = 0.3;
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;

        settings.atomNeuronTransformation = Settings.TransformationFcn.SIGMOID;     // TanH are much better, but this is the diffcheck test
        settings.ruleNeuronTransformation = Settings.TransformationFcn.SIGMOID;

        settings.errorFunction = Settings.ErrorFcn.SQUARED_DIFF;
        settings.inferOutputFcns = false;
        settings.squishLastLayer = false;

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = false;
        settings.chainPruning = true;
        settings.isoValueCompression = false;
        settings.storeNotShow = true;

        Pair<Pipeline, ?> main = Main.main(argsVect, settings);
        DetailedClassificationResults results = (DetailedClassificationResults) main.s;
        assertEquals(0.619042613350086, results.dispersion, 0.01);
    }

    /**
     * true results :- train: accuracy: 90.43%, disp: 0.6269538938163348, error: AVG(SQUARED_DIFF) = 0.0764636179, (best thresh acc: 92.02%) (maj. 66.49%), (AUC-ROC: 0.9601269841), (AUC-PR: 0.978976231)
     * Total time: 00:10:02:965
     * <p>
     * btw. ADAM s lr=0.01 tu bezi brutalne dobre
     *
     * @throws Exception
     */
    @TestAnnotations.Slow
    public void mutagen_diffcheck_vectorized_compressed() throws Exception {

        Settings settings = new Settings();

        settings.trainValidationPercentage = 1.0;

        settings.initDistribution = Settings.InitDistribution.UNIFORM;
//        settings.plotProgress = 20;

        settings.seed = 0;
        settings.maxCumEpochCount = 1000;
        settings.resultsRecalculationEpochae = 10;
        settings.shuffleEachEpoch = true;
        settings.debugSampleOutputs = false;
        settings.calculateBestThreshold = true;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.initLearningRate = 0.3;
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;

        settings.atomNeuronTransformation = Settings.TransformationFcn.SIGMOID;     // TanH are much better, but this is the diffcheck test
        settings.ruleNeuronTransformation = Settings.TransformationFcn.SIGMOID;

        settings.errorFunction = Settings.ErrorFcn.SQUARED_DIFF;
        settings.inferOutputFcns = false;
        settings.squishLastLayer = false;

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = false;
        settings.chainPruning = true;
        settings.isoValueCompression = true;
        settings.storeNotShow = true;

        Pair<Pipeline, ?> main = Main.main(argsVect, settings);
        DetailedClassificationResults results = (DetailedClassificationResults) main.s;
        assertEquals(0.6109891807533527, results.dispersion, 0.01);
    }

    /**
     * with result: accuracy: 87.77%, disp: 0.49502512040795565, error: 0.1022808575, (best thresh acc: 89.36%) (maj. 66.49%), (AUC-ROC: 0.9306666667), (AUC-PR: 0.9653845754)
     * Total time: 00:10:05:965
     *
     * @throws Exception
     */
    @TestAnnotations.Slow
    public void mutagen_diffcheck_vectorized_compressed_newdata() throws Exception {

        Settings settings = new Settings();

        settings.trainValidationPercentage = 1.0;

        settings.initDistribution = Settings.InitDistribution.UNIFORM;
        settings.plotProgress = 20;

        settings.seed = 0;
        settings.maxCumEpochCount = 1000;
        settings.resultsRecalculationEpochae = 10;
        settings.shuffleEachEpoch = true;
        settings.debugSampleOutputs = false;
        settings.calculateBestThreshold = true;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.initLearningRate = 0.3;
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;

        settings.atomNeuronTransformation = Settings.TransformationFcn.SIGMOID;     // TanH are much better, but this is the diffcheck test
        settings.ruleNeuronTransformation = Settings.TransformationFcn.SIGMOID;
//
        settings.errorFunction = Settings.ErrorFcn.SQUARED_DIFF;
        settings.inferOutputFcns = false;
        settings.squishLastLayer = false;

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = false;
        settings.chainPruning = true;
        settings.isoValueCompression = true;
        settings.storeNotShow = true;

//        settings.groundingMode = Settings.GroundingMode.GLOBAL;

        Pair<Pipeline, ?> main = Main.main(argsNew, settings);
        DetailedClassificationResults results = (DetailedClassificationResults) main.s;
        assertEquals(0.6109891807533523, results.dispersion, 0.01);
    }

}