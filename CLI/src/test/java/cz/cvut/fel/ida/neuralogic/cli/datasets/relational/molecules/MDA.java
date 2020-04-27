package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.util.logging.Logger;

public class MDA {
    private static final Logger LOG = Logger.getLogger(MDA.class.getName());

    @TestAnnotations.Slow
    public void defalutPerformance() throws Exception {

        Settings.inputFilesSuffix = "";

        Settings settings = Settings.forSlowTest();
        settings.seed = 0;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.001;

        settings.islearnRateDecay = false;

        settings.plotProgress = 10;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/MDA_MB_231_ATCC", "-t ./templates/template_gnnW_l3w3_bonds.txt"), settings);
    }

    @TestAnnotations.Slow
    public void alternativePerformance() throws Exception {

//        Settings.inputFilesSuffix = "";

        Settings settings = Settings.forSlowTest();
        settings.seed = 0;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.001;

        settings.islearnRateDecay = false;

        settings.plotProgress = 5;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/MDA_MB_231_ATCC/alternative"), settings);
    }

    @TestAnnotations.Slow
    public void featuresPerformance() throws Exception {

        Settings settings = Settings.forSlowTest();
        settings.seed = 0;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.000015;

        settings.islearnRateDecay = false;

        settings.crossvalidation = true;
        settings.appLimitSamples = 10;

        settings.plotProgress = 5;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/MDA_MB_231_ATCC/features", "-t ./templates/template_gnnl3W10.txt"), settings);
    }

}
