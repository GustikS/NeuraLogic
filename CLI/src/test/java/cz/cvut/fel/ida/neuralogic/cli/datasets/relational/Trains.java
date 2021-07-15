package cz.cvut.fel.ida.neuralogic.cli.datasets.relational;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Trains {
    private static final Logger LOG = Logger.getLogger(Trains.class.getName());

    static String dataset = "relational/trains";

    @TestAnnotations.Slow
    public void embeddings() throws Exception {
        Settings settings = Settings.forSlowTest();
//        settings.debugPipeline = true;
//        settings.drawing = true;
        settings.squishLastLayer = false;
        settings.plotProgress = 1;
        settings.maxCumEpochCount = 10000;
        Main.main(getDatasetArgs(dataset,"-t ./template.txt"), settings);
    }
}
