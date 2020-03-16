package grounding.debugging;

import org.junit.Test;
import cz.cvut.fel.ida.setup.Settings;
import utils.Runner;
import utils.logging.Logging;

public class GroundingDebuggerTest {
    @Test
    public void family() {
        Logging logging = Logging.initLogging();
        String[] args = new String("-path ./resources/datasets/family").split(" ");
        Settings settings = new Settings();
        settings.intermediateDebug = false;
//        Settings.loggingLevel = Level.WARNING;
        GroundingDebugger groundingDebugger = new GroundingDebugger(Runner.getSources(args, settings), settings);
        groundingDebugger.executeDebug();
    }
}