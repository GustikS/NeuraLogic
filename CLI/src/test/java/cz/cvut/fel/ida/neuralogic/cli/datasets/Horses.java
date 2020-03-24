package cz.cvut.fel.ida.neuralogic.cli.datasets;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class Horses {
    private static final Logger LOG = Logger.getLogger(Horses.class.getName());

    @TestAnnotations.Slow
    public void horsesSetting() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.maxCumEpochCount = 10;
        Main.main(getDatasetArgs("simple/family"), settings);
    }
}