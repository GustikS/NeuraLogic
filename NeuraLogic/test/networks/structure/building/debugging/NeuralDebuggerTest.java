package networks.structure.building.debugging;

import org.junit.Test;
import settings.Settings;
import utils.logging.Logging;

import java.util.logging.Level;

public class NeuralDebuggerTest {

    @Test
    public void family(){
        Logging logging = Logging.initLogging();
        String[] args = new String("-path ./resources/datasets/family").split(" ");
        Settings settings = new Settings();
        settings.intermediateDebug = true;
        Settings.loggingLevel = Level.WARNING;
        NeuralDebugger neuralDebugger = new NeuralDebugger(args, settings);
        neuralDebugger.executeDebug();
    }
}