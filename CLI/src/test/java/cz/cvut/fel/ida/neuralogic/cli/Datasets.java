package cz.cvut.fel.ida.neuralogic.cli;

import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.junit.jupiter.api.Disabled;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.splitArgs;

@TestAnnotations.Functional
public class Datasets {
    private static final Logger LOG = Logger.getLogger(Datasets.class.getName());

    @TestAnnotations.Slow
    @Disabled
    public void mutagenesis() throws Exception {
        String resourcePath = Utilities.getResourcePath("relational/molecules/mutagenesis");
        String args = "-sd " + resourcePath;
        Settings settings = Settings.forBigTest();
        settings.maxCumEpochCount = 1000;
        Main.main(splitArgs(args), settings);
    }

}
