package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.util.logging.Logger;

public class DMS114 {
    private static final Logger LOG = Logger.getLogger(DMS114.class.getName());

    @TestAnnotations.Slow
    public void defaultMutagenPerformanceADAM_unifiedTemplate() throws Exception {

        Settings settings = Settings.forSlowTest();
        settings.seed = 0;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.001;

        settings.islearnRateDecay = false;

        settings.plotProgress = 5;

        Pair<Pipeline, ?> results = Main.main(Utilities.getDatasetArgs("relational/molecules/DMS_114", " -t ./templates/template_gnnW10.txt"), settings);
    }

    @TestAnnotations.Slow
    public void valsplitEmbeddings() throws Exception {

        Settings settings = Settings.forSlowTest();
        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.initLearningRate = 0.001;

        settings.islearnRateDecay = false;

        settings.plotProgress = 5;

        Pair<Pipeline, ?> results = Main.main(Utilities.splitArgs("-sd /home/gusta/data/datasets/jair/first10/mol2types/gnnlrnn/DMS_114 -fp fold -t /home/gusta/googledrive/Github/NeuraLogic/Resources/datasets/relational/molecules/DMS_114/fold0/template.txt"), settings);
    }
}
