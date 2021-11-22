package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.splitArgs;

public class FunctionalTests {
    private static final Logger LOG = Logger.getLogger(FunctionalTests.class.getName());

    @TestAnnotations.Parameterized
    @ValueSource(strings = {
            "simple/family",
            "neural/xor/naive",
            "relational/molecules/mutagenesis",
            "relational/kbs/nations"
    })
    @Tag("Fast")
    void checkAvailableUseCases(String argString) throws Exception {
        String resourcePath = Utilities.getResourcePath(argString);
        LOG.info("Testing " + resourcePath);
        String args = "-lim 1 -ts 1 -sd " + resourcePath;
        Main.mainExc(splitArgs(args));
    }
}