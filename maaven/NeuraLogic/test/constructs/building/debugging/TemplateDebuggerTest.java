package constructs.building.debugging;

import cz.cvut.fel.ida.pipelines.constructs.template.debugging.TemplateDebugger;
import org.junit.Test;
import cz.cvut.fel.ida.setup.Settings;
import utils.Runner;
import utils.logging.Logging;

import java.util.logging.Level;

public class TemplateDebuggerTest {

    @Test
    public void family() {
        Logging logging = Logging.initLogging(Level.FINE);
        String[] args = "-path ./resources/datasets/family".split(" ");
        Settings settings = new Settings();
        settings.debugTemplate = true;
        settings.intermediateDebug = false;
        TemplateDebugger templateDebugger = new TemplateDebugger(Runner.getSources(args, settings), settings);
        templateDebugger.executeDebug();
    }
}