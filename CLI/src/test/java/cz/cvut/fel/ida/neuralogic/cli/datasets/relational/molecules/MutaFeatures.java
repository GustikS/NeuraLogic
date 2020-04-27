package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.util.logging.Logger;

public class MutaFeatures {
    private static final Logger LOG = Logger.getLogger(MutaFeatures.class.getName());

    @TestAnnotations.Slow
    public void defaultMutagenFeatures() throws Exception {

        Settings settings = Settings.forSlowTest();
        settings.seed = 0;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.0001;

        settings.islearnRateDecay = false;

        settings.plotProgress = 10;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/mutag_188", " -t ./template_mixed.txt"), settings);
    }
}
