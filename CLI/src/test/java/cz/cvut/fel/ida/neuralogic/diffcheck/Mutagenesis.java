package cz.cvut.fel.ida.neuralogic.diffcheck;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.TrainingDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class Mutagenesis {
    private static final Logger LOG = Logger.getLogger(Mutagenesis.class.getName());

    String[] args2ex = Utilities.getDatasetArgs("relational/molecules/mutagenesis/diffcheck", "-e examples2only.txt");
    String[] argsAll = Utilities.getDatasetArgs("relational/molecules/mutagenesis/diffcheck", "-q allQueries.txt");

    @Test
    public void mutagen_diffcheck() throws Exception {
        Logging logging = Logging.initLogging();

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.CONSTANT;

        settings.seed = 2;  //the seed shouldn't matter here!
        settings.shuffleBeforeFoldSplit = false;
        settings.shuffleBeforeTraining = false;
        settings.shuffleEachEpoch = false;
        settings.debugSampleOutputs = true;

        settings.maxCumEpochCount = 1000;
        settings.resultsRecalculationEpochae = 1;
        settings.calculateBestThreshold = true;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.initLearningRate = 0.3;
        settings.iterationMode = Settings.IterationMode.DFS_RECURSIVE;

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = false;
        settings.isoValueCompression = false;
        settings.chainPruning = false;
        settings.storeNotShow = true;

        settings.debugExporting = true;

        TrainingDebugger trainingDebugger = new TrainingDebugger(Runner.getSources(args2ex, settings), settings);
        trainingDebugger.executeDebug();
    }

    @Test
    public void mutagen_diffcheck_mods() throws Exception {
        Logging logging = Logging.initLogging();
        String[] args = ("-e ./resources/datasets/relational/molecules/mutagenesis/examples " +
                "-q ./resources/datasets/relational/molecules/mutagenesis/queries " +
//                "-t ./resources/datasets/relational/molecules/mutagenesis/template_new.txt").split(" ");
                "-t ./resources/datasets/relational/molecules/mutagenesis/template_vector_cross").split(" ");

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.CONSTANT;

        settings.seed = 0;  //the seed shouldn't matter here!
        settings.shuffleBeforeFoldSplit = false;
        settings.shuffleBeforeTraining = false;
        settings.shuffleEachEpoch = false;

        settings.debugSampleOutputs = true;
        settings.storeNotShow = true;

        settings.initLearningRate = 0.3;
        settings.maxCumEpochCount = 100;
        settings.resultsRecalculationEpochae = 1;
        settings.calculateBestThreshold = false;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;  //tested as equivalent to DFS

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = true;
        settings.isoValueCompression = false;
        settings.chainPruning = true;

        TrainingDebugger trainingDebugger = new TrainingDebugger(Runner.getSources(args, settings), settings);
        trainingDebugger.executeDebug();
    }

    @Test
    public void mutagen_diffcheck_uniform() throws Exception {
        Logging logging = Logging.initLogging();
        String[] args = ("-e ./resources/datasets/relational/molecules/mutagenesis/examples " +
                "-q ./resources/datasets/relational/molecules/mutagenesis/queries " +
//                "-t ./resources/datasets/relational/molecules/mutagenesis/template_new.txt").split(" ");
                "-t ./resources/datasets/relational/molecules/mutagenesis/template_vector_cross").split(" ");

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.UNIFORM;

        settings.seed = 0;  //the seed shouldn't matter here!
        settings.shuffleBeforeFoldSplit = false;
        settings.shuffleBeforeTraining = true;
        settings.shuffleEachEpoch = true;

        settings.debugSampleOutputs = false;
        settings.storeNotShow = true;

        settings.initLearningRate = 0.3;
        settings.maxCumEpochCount = 100;
        settings.resultsRecalculationEpochae = 1;
        settings.calculateBestThreshold = false;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.SGD);
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;  //tested as equivalent to DFS

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = true;
        settings.isoValueCompression = false;
        settings.chainPruning = true;

        TrainingDebugger trainingDebugger = new TrainingDebugger(Runner.getSources(args, settings), settings);
        trainingDebugger.executeDebug();
    }
}