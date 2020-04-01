package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class EColi {
    private static final Logger LOG = Logger.getLogger(EColi.class.getName());
    static String dataset = "relational/molecules/e_coli";

    @TestAnnotations.Slow
    public void defaultEcoliPerformance() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.maxCumEpochCount = 1000;
        settings.plotProgress = 5;

        settings.trainValidationPercentage = 0.3;

        settings.appLimitSamples = 500;
        settings.isoValueCompression = true;
        settings.chainPruning = true;
        settings.processMetadata = true;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./template10.txt"), settings);
    }

    @TestAnnotations.Slow
    public void defaultEcoliPerformanceCross() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.maxCumEpochCount = 100;
        settings.plotProgress = 30;

        settings.appLimitSamples = 1000;
        settings.isoValueCompression = false;
        settings.chainPruning = false;

        settings.errorFunction = Settings.ErrorFcn.CROSSENTROPY;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./template_cross.txt"), settings);
    }



    @TestAnnotations.Slow
    public void defaultEcoliPerformanceGNN() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.maxCumEpochCount = 10000;

        settings.appLimitSamples = 10000;
        settings.isoValueCompression = false;
        settings.chainPruning = false;
        settings.processMetadata = true;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./template_gnn.txt"), settings);
    }


    @TestAnnotations.Slow
    public void defaultEcoliPerformanceCross_08AUC() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.maxCumEpochCount = 500;

        settings.appLimitSamples = -1;
        settings.isoValueCompression = false;
        settings.chainPruning = false;
        settings.processMetadata = true;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./template_cross.txt"), settings);
    }
}
