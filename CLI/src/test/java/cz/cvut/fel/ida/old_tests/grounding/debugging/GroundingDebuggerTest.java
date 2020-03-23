package cz.cvut.fel.ida.old_tests.grounding.debugging;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.GroundingDebugger;
import cz.cvut.fel.ida.setup.Settings;
import org.junit.jupiter.api.Test;

public class GroundingDebuggerTest {
    @Test
    public void family() throws Exception {
        Logging logging = Logging.initLogging();
        String[] args = new String("-path ./resources/datasets/family").split(" ");
        Settings settings = new Settings();
        settings.intermediateDebug = false;
//        Settings.loggingLevel = Level.WARNING;
        GroundingDebugger groundingDebugger = new GroundingDebugger(Runner.getSources(args, settings), settings);
        groundingDebugger.executeDebug();
    }
}