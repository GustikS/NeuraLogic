package cz.cvut.fel.ida.old_tests.pipelines.debugging;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.TemplateDebugger;
import cz.cvut.fel.ida.setup.Settings;
import org.junit.jupiter.api.Test;

class TemplateDebuggerTest {
    @Test
    public void family() throws Exception {
        Logging logging = Logging.initLogging();
        String[] args = "-path ./resources/datasets/family".split(" ");
        Settings settings = new Settings();
        settings.debugTemplate = true;
        settings.intermediateDebug = false;
        TemplateDebugger templateDebugger = new TemplateDebugger(Runner.getSources(args, settings), settings);
        templateDebugger.executeDebug();
    }
}