package cz.cvut.fel.ida.old_tests.utils.drawing;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.GroundingDebugger;
import cz.cvut.fel.ida.setup.Settings;
import org.junit.Test;

public class GroundingDrawerTest {
    @Test
    public void family() throws Exception {
        Logging logging = Logging.initLogging();
        String[] args = new String("-path ./resources/datasets/simple/family").split(" ");
        Settings settings = new Settings();
        settings.intermediateDebug = false;
        settings.drawing = true;
//        Settings.loggingLevel = Level.WARNING;
        GroundingDebugger groundingDebugger = new GroundingDebugger(Runner.getSources(args, settings), settings);
        groundingDebugger.executeDebug();
    }
}