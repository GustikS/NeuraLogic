package cz.cvut.fel.ida.neuralogic.cli;

import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.splitArgs;

@TestAnnotations.Slow
public class FunctionalTests {
    private static final Logger LOG = Logger.getLogger(FunctionalTests.class.getName());


    @TestAnnotations.Parameterized
    @ValueSource(strings = {
            "simple/family",
            "neural/xor/naive",
            "relational/molecules/mutagenesis",
            "relational/kbs/kinships"
    })
    @Disabled
    void checkAvailableUseCases(String argString) throws Exception {
        String resourcePath = Utilities.getResourcePath(argString);
        LOG.info("Testing " + resourcePath);
        String args = "-lim 1 -ts 0 -sd " + resourcePath;
        Main.mainExc(splitArgs(args));
    }

    @TestAnnotations.Slow
    @Disabled
    public void mutagenesis() throws Exception {
        String resourcePath = Utilities.getResourcePath("relational/molecules/mutagenesis");
        String args = "-ts 10 -sd " + resourcePath;
        Main.mainExc(splitArgs(args));
    }

    @TestAnnotations.Slow
//    @Disabled
    public void mutagenesisSetting() throws Exception {
        String resourcePath = Utilities.getResourcePath("relational/molecules/mutagenesis");
        String args = "-sd " + resourcePath;
        Settings settings = Settings.forBigTest();
        settings.maxCumEpochCount = 10;
        Main.main(splitArgs(args), settings);
    }
}
