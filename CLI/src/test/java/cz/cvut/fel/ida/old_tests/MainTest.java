package cz.cvut.fel.ida.old_tests;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Level;

public class MainTest {

    @TestAnnotations.AdHoc
    public void parsing() throws Exception {
        String[] args = new String("-t ./resources/parsing/test_template -q ./resources/parsing/queries").split(" ");
        Main.main(args);
    }

    @TestAnnotations.AdHoc
    public void xor_vectorized() throws Exception {
        String[] args = new String("-path ./resources/datasets/neural/xor/vectorized").split(" ");
        Settings settings = new Settings();
        settings.shuffleBeforeTraining = false;
        settings.seed = 0;
        settings.initLearningRate = 0.1;
        settings.maxCumEpochCount = 1000;
        settings.resultsRecalculationEpochae = 20;
        settings.neuralNetsPostProcessing = false;  //crucial to be True!
        settings.chainPruning = true;
        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        Settings.loggingLevel = Level.FINER;
        Main.main(args, settings);
    }


    @TestAnnotations.AdHoc
    public void xor_basic() throws Exception {
        String[] args = new String("-path ./resources/datasets/neural/xor/naive").split(" ");
        Settings settings = new Settings();
        settings.shuffleBeforeTraining = false;
        settings.seed = 0;
        settings.initLearningRate = 0.1;
        settings.maxCumEpochCount = 100000;
        settings.neuralNetsPostProcessing = true;  //crucial to be True!
        settings.chainPruning = true;
        Settings.loggingLevel = Level.FINER;
        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void xor_solution() throws Exception {
        String[] args = new String("-path ./resources/datasets/neural/xor/solution").split(" ");
        Settings settings = new Settings();
        settings.seed = 0;
        Settings.loggingLevel = Level.FINEST;
        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void family() throws Exception {
        String[] args = new String("-e ./resources/datasets/family/examples -t ./resources/datasets/family/template -q ./resources/datasets/family/queries").split(" ");
        Settings settings = new Settings();
        settings.shuffleBeforeTraining = false;
        settings.initLearningRate = 1;
        settings.maxCumEpochCount = 1000;
        Settings.loggingLevel = Level.FINEST;
        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void mutagenesis() throws Exception {
        String[] args = new String("-e ./resources/datasets/mutagenesis/examples -t ./resources/datasets/mutagenesis/template_old -q ./resources/datasets/mutagenesis/queries").split(" ");

        Settings settings = new Settings();
        Settings.loggingLevel = Level.FINER;
        //settings.limitSamples = 10;
        settings.maxCumEpochCount = 10000;
        //settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = true;
        settings.chainPruning = false;
        settings.isoValueCompression = true;

        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void mutamini() throws Exception {
        String[] args = new String("-e ./resources/datasets/muta_mini/examples -t ./resources/datasets/muta_mini/template_new").split(" ");

        Settings settings = new Settings();
        Settings.loggingLevel = Level.FINER;
        settings.initLearningRate = 0.2;
        settings.maxCumEpochCount = 10000;
        settings.shuffleBeforeTraining = false;
        //settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = true;
        settings.chainPruning = true;
        settings.seed = 0;

//        settings.ruleNeuronActivation = Settings.ActivationFcn.IDENTITY;
//        settings.atomNeuronActivation = Settings.ActivationFcn.IDENTITY;
        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void mutanokappa() throws Exception {
        String[] args = new String("-e ./resources/datasets/mutagenesis/examples -t ./resources/datasets/mutagenesis/template_new -q ./resources/datasets/mutagenesis/queries").split(" ");

        Settings settings = new Settings();
        Settings.loggingLevel = Level.FINER;
        settings.initLearningRate = 0.1;
        settings.maxCumEpochCount = 20;
        settings.shuffleBeforeTraining = false;
        //settings.oneQueryPerExample = true;

        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void p388() throws Exception {
        String[] args = new String("-e ./resources/datasets/p388/examples -t ./resources/datasets/p388/normalFully").split(" ");

        Settings settings = new Settings();
        Settings.loggingLevel = Level.FINER;
        settings.initLearningRate = 0.1;
        settings.maxCumEpochCount = 2000;
        settings.shuffleBeforeTraining = false;
        //settings.oneQueryPerExample = true;

        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void jair_revived() throws Exception {
        String[] args = ("-e ./resources/datasets/relational/molecules/mutagenesis/examples " +
                "-q ./resources/datasets/relational/molecules/mutagenesis/queries " +
                "-t ./resources/datasets/relational/molecules/mutagenesis/template_vector_cross " +
                " -out ./out/jair-adam -ts 100 -xval 5").split(" ");

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.UNIFORM;

        settings.shuffleBeforeFoldSplit = true;
        settings.shuffleBeforeTraining = true;
        settings.shuffleEachEpoch = true;

        settings.debugSampleOutputs = false;
        settings.storeNotShow = true;

        settings.initLearningRate = 0.01;
        settings.maxCumEpochCount = 100;
        settings.resultsRecalculationEpochae = 1;
        settings.calculateBestThreshold = false;
//        settings.appLimitSamples = 100;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;  //tested as equivalent to DFS

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = true;
        settings.isoValueCompression = false;
        settings.chainPruning = true;

        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void muta_traintest() throws Exception {
        String[] args = ("-e ./resources/datasets/relational/molecules/mutagenesis/split/train " +
                "-te ./resources/datasets/relational/molecules/mutagenesis/split/test " +
                "-t ./resources/datasets/relational/molecules/mutagenesis/template_vector_cross " +
                " -out ./out/muta-tt -ts 100").split(" ");

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.UNIFORM;

        settings.shuffleBeforeFoldSplit = false;
        settings.shuffleBeforeTraining = false;
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

        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void muta_gnn() throws Exception {
        String[] args = ("-e ./resources/datasets/relational/molecules/mutagenesis/examples " +
                "-q ./resources/datasets/relational/molecules/mutagenesis/queries " +
                "-t ./resources/datasets/relational/molecules/template_unified_gnn_bad" +
                " -out ./out/muta-gnn3 -xval 5").split(" ");

        Settings settings = new Settings();

        settings.initDistribution = Settings.InitDistribution.UNIFORM;
        settings.aggNeuronAggregation = Settings.CombinationFcn.AVG;

        settings.seed = 0;
        settings.initLearningRate = 0.01;
        settings.maxCumEpochCount = 100;
        settings.resultsRecalculationEpochae = 10;
        settings.shuffleEachEpoch = true;
        settings.debugSampleOutputs = false;
        settings.calculateBestThreshold = true;
        settings.initializer = Settings.InitSet.SIMPLE;
        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.iterationMode = Settings.IterationMode.TOPOLOGIC;

        settings.oneQueryPerExample = true;
        settings.neuralNetsPostProcessing = true;
        settings.chainPruning = true;

        settings.isoValueCompression = true;
        settings.structuralIsoCompression = false;
        settings.isoValueInits = 1;
        settings.isoDecimals = 15;

        settings.foldsCount = 5;
        settings.storeNotShow = false;

//        settings.debugTemplate = true;
//        settings.debugPipeline = true;
//        settings.debugNeuralization = true;
        settings.drawing = true;

        Main.main(args, settings);
    }

    @TestAnnotations.AdHoc
    public void mda() throws Exception {
        String[] args = ("-e ./resources/datasets/relational/molecules/MDA_MB_231_ATCC/examples " +
                "-q ./resources/datasets/relational/molecules/MDA_MB_231_ATCC/queries " +
                "-t ./resources/datasets/relational/molecules/template_unified_bad -isoinits 1" +
                " -isocheck 1 -xval 5 -ts 10 -iso 12"+
                " -out ./out/mda_rep").split(" ");

        Main.main(args);
    }

    @TestAnnotations.AdHoc
    public void loadFromConsole() throws Exception {
        String[] args = ("-path ./resources/datasets/relational/molecules/mutagenesis" +
                " -settings ./resources/settings/settings.json" +
                " -out ./out/loading").split(" ");

        Main.main(args);
    }


    @TestAnnotations.AdHoc
    public void loadFromFiles() throws Exception {
        String[] args = (
                " -settings ./resources/settings/settings.json" +
                " -sources ./resources/settings/sources.json" +
                " -out ./out/jsons").split(" ");

        Main.main(args);
    }

    @TestAnnotations.AdHoc
    public void compression() throws Exception {
        String[] args = ("-sourcesDir ./resources/datasets/relational/molecules/mutagenesis " +
                "-opt sgd -lr 0.3 -iso 10 -xval 5 -out ./out/compress").split(" ");

//        Settings settings = new Settings();
//        settings.crossvalidation = false;

//
//        settings.isoValueCompression = true;
//        settings.losslessIsoCompression = true;
//        settings.isoValueInits = 1;
//        settings.isoDecimals = 10;

        Main.main(args);
    }

    @TestAnnotations.AdHoc
    public void rciTest() throws Exception {
        String[] args = ("-t ./template.txt" +
                " -path ./resources/datasets/relational/molecules/mutagenesis" +
                " -opt adam -lr 0.01 -ts 10 -limit 10" +
                " -out ./out/rci").split(" ");

        Main.main(args);
    }

    @TestAnnotations.AdHoc
    public void kinships() throws Exception {
        Logging logging = Logging.initLogging(new Settings());
        String[] args = ("-q ./resources/datasets/relational/kbs/kinships/queries " +
                "-e ./resources/datasets/relational/kbs/kinships/examples " +
                "-t ./resources/datasets/relational/kbs/kinships/template_embeddings"+
                " -out ./out/kinships_test").split(" ");

        Settings settings = new Settings();

//        settings.neuralNetsPostProcessing = false;
        settings.crossvalidation = true;
        settings.maxCumEpochCount = 10;

        settings.trainFoldsIsolation = true;

        settings.chainPruning = true;
        settings.isoValueCompression = true;
        settings.structuralIsoCompression = false;

        Main.main(args, settings);
    }
}
