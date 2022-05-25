package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TranspositionTest {

    @TestAnnotations.Fast
    public void transpose() throws Exception {
        Settings settings = new Settings();
        settings.chainPruning = false;
        settings.isoValueCompression = false;
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs("debug/transpose"), settings);
        System.out.println(results);
        assertNotNull(results);
    }

}