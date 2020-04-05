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

    /**
     * DO NOT TOUCH THIS!!
     *
     * @throws Exception
     */
    @TestAnnotations.Medium
    public void mutagen_diffcheck2examplesEXACT() throws Exception {
        Logging logging = Logging.initLogging();

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.CONSTANT;

        // the seed 3 matches the old version with seed 1 with disp value = 0.0014794164025840328
        settings.seed = 0;  //the seed matters only from 15th decimal place further and there are only 2-3 variants (probably orderings in summations)
        settings.shuffleBeforeFoldSplit = false;
        settings.shuffleBeforeTraining = false;
        settings.shuffleEachEpoch = false;
        settings.debugSampleOutputs = true;
        settings.trainValidationPercentage = 1.0;

        settings.maxCumEpochCount = 100;
        settings.resultsRecalculationEpochae = 1;

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
        assertEquals(0.0014794164025840328, results.dispersion, 0.0000000000000001);
    }

    @TestAnnotations.Slow
    public void mutagen_diffcheck_standard() throws Exception {

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.UNIFORM;

        settings.trainValidationPercentage = 1.0;
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
        settings.detailedResults = true;

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = false;
        settings.chainPruning = true;
        settings.isoValueCompression = false;
        settings.storeNotShow = true;

        Pair<Pipeline, ?> main = Main.main(argsAll, settings);
        DetailedClassificationResults results = (DetailedClassificationResults) main.s;
//        assertEquals(results.dispersion, 0.6590494405160751, 0.01);
    }

    /**
     * true results :- train: accuracy: 90.43%, disp: 0.6190426133500868, error: AVG(SQUARED_DIFF) = 0.0739864, (best thresh acc: 92.02%) (maj. 66.49%), (AUC-ROC: 0.9564444444), (AUC-PR: 0.9756295178)
     * Total time: 00:36:39:985
     * @throws Exception
     */
    @TestAnnotations.Slow
    public void mutagen_diffcheck_vectorized() throws Exception {

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
        settings.detailedResults = true;

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = false;
        settings.chainPruning = true;
        settings.isoValueCompression = false;
        settings.storeNotShow = true;

        Pair<Pipeline, ?> main = Main.main(argsVect, settings);
        DetailedClassificationResults results = (DetailedClassificationResults) main.s;
        assertEquals(results.dispersion, 0.619042613350086, 0.01);
    }
}