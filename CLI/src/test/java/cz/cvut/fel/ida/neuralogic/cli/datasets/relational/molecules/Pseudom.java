package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.molecules;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Pseudom {
    private static final Logger LOG = Logger.getLogger(Pseudom.class.getName());

    static String dataset = "relational/molecules/pseudomon";

    @TestAnnotations.Slow
    public void trainValTestCrossval() throws Exception {
        Settings settings = Settings.forSlowTest();

        settings.seed = 1;
//        settings.drawing = true;
//        settings.debugPipeline = true;
//        settings.storeNotShow = true;
        settings.isoValueCompression = true;

        settings.appLimitSamples = 100;

        settings.maxCumEpochCount = 10;
        settings.plotProgress = 10;

        Pair<Pipeline, ?> results = Main.main(getDatasetArgs(dataset, " -t ./template_gnnW.txt -fp fold  -e ./train.csv.txt -ve ./dev.csv.txt -te ./test.csv.txt"), settings);
    }
}
