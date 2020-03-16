package utils.drawing;

import cz.cvut.fel.ida.pipelines.grounding.debugging.GroundingDebugger;
import org.junit.Test;
import cz.cvut.fel.ida.setup.Settings;
import utils.Runner;
import utils.logging.Logging;

public class GroundingDrawerTest {
    @Test
    public void family() {
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