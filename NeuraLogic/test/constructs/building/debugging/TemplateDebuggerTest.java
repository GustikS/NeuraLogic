package constructs.building.debugging;

import org.junit.Test;
import settings.Settings;
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
        TemplateDebugger templateDebugger = new TemplateDebugger(args, settings);
        templateDebugger.executeDebug();
    }
}