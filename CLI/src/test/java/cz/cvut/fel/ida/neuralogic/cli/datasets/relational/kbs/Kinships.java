package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.kbs;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Kinships {
    private static final Logger LOG = Logger.getLogger(Kinships.class.getName());

    static String dataset = "relational/kbs/kinships";

    @TestAnnotations.Slow
    public void basic() throws Exception {
        Settings settings = Settings.forFastTest();
        settings.appLimitSamples = -1;
        Main.main(getDatasetArgs(dataset), settings);
    }
}
