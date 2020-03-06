package constructs.building.debugging;

import constructs.template.debugging.TemplateDebugger;
import org.junit.Test;
import settings.Settings;
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