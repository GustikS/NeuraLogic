package cz.cvut.fel.ida.neuralogic.revised.debugging;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.NeuralDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.JavaExporter;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.setup.Settings.ExportFileType.JAVA;
import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NeuralDebuggerTest {

    private static final java.util.logging.Logger LOG = Logger.getLogger(NeuralDebuggerTest.class.getName());

    @TestAnnotations.Slow
    public void family() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.intermediateDebug = false;
        settings.exportType = JAVA;
        settings.debugExporting = true;
        NeuralDebugger neuralDebugger = new NeuralDebugger(Runner.getSources(getDatasetArgs("simple/family"), settings), settings);
        neuralDebugger.executeDebug();
        assertTrue(Paths.get(Logging.logFile.toString(), "export", "debug", NeuralDebugger.class.getSimpleName() + ".java").toFile().exists());
    }

    @TestAnnotations.Slow
    public void loadGroundSamples() throws Exception {
        List<NeuralSample> neuralSamples = new JavaExporter().importListFrom(Paths.get("../Resources/datasets/simple/family/mock/" + NeuralDebugger.class.getSimpleName() + ".java"), NeuralSample.class);
        for (NeuralSample neuralSample : neuralSamples) {
            LOG.fine(neuralSample.exportToJson());
            assertNotNull(neuralSample.query.evidence);
        }
    }
}