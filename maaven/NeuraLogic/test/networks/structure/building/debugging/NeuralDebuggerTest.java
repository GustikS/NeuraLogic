package networks.structure.building.debugging;

import org.junit.Test;
import cz.cvut.fel.ida.setup.Settings;
import utils.Runner;
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
        NeuralDebugger neuralDebugger = new NeuralDebugger(Runner.getSources(args, settings), settings);
        neuralDebugger.executeDebug();
    }
}