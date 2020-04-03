package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class EColiFeatures {
    private static final Logger LOG = Logger.getLogger(EColiFeatures.class.getName());

    static String dataset = "relational/molecules/e_coli/features";

    @TestAnnotations.Slow
    public void defaultEcoliPerformance() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;

        settings.setOptimizer(Settings.OptimizerSet.ADAM);
        settings.maxCumEpochCount = 10000;
        settings.plotProgress = 5;
        settings.calculateBestThreshold = false;
        settings.trainValidationPercentage = 0.9;

//        settings.appLimitSamples = 200;
        settings.isoValueCompression = true;
        settings.chainPruning = false;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t ./templates/template.txt"), settings);
    }

    @TestAnnotations.Parameterized
    @ValueSource(strings = {
//            "./templates/template.txt",
//            "./templates/template10.txt",
//            "./templates/template100.txt",
//            "./templates/template_cross.txt",
//            "./templates/template_cross4.txt",
//            "./templates/template_gnn.txt",
            "./templates/template_gnn_shallow.txt",
            "./templates/template_gnn_simple.txt",
            "./templates/template_gnnW.txt",
            "./templates/template_gnnW10.txt",
            "./templates/template_gnnW100.txt",
            "./templates/templateW.txt",
            "./templates/templateW10.txt",
            "./templates/templateW100.txt"
    })
    public void testTemplates(String template) throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.maxCumEpochCount = 10;
        settings.appLimitSamples = 50;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, "-t " + template), settings);
    }
}
