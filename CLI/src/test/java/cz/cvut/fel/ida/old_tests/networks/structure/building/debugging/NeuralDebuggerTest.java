package cz.cvut.fel.ida.old_tests.networks.structure.building.debugging;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.NeuralDebugger;
import cz.cvut.fel.ida.setup.Settings;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;

public class NeuralDebuggerTest {

    @Test
    public void family() throws Exception {
        Logging logging = Logging.initLogging();
        String[] args = new String("-path .resources/datasets/family").split(" ");
        Settings settings = new Settings();
        settings.intermediateDebug = true;
        Settings.loggingLevel = Level.WARNING;
        NeuralDebugger neuralDebugger = new NeuralDebugger(Runner.getSources(args, settings), settings);
        neuralDebugger.executeDebug();
    }
}