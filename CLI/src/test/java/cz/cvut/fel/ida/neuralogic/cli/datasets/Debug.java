package cz.cvut.fel.ida.neuralogic.cli.datasets;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Debug {
    private static final Logger LOG = Logger.getLogger(Debug.class.getName());

    static String dataset = "neural/xor/relational_debug";

    @TestAnnotations.Fast
    public void xor() throws Exception {
        Settings settings = Settings.forSlowTest();
//        settings.debugPipeline = true;
//        settings.drawing = true;
//        settings.squishLastLayer = false;
//        settings.plotProgress = 1;
        settings.maxCumEpochCount = 10;
        Main.main(getDatasetArgs(dataset,"-t ./template.txt"), settings);
    }
}
