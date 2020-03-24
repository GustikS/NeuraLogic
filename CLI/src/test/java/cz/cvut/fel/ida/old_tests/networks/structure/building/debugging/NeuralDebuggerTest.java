package cz.cvut.fel.ida.old_tests.networks.structure.building.debugging;

import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.NeuralDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.JavaExporter;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.nio.file.Paths;
import java.util.List;

import static cz.cvut.fel.ida.setup.Settings.ExportFileType.JAVA;
import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class NeuralDebuggerTest {

    @TestAnnotations.Slow
    public void family() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.intermediateDebug = false;
        settings.exportType = JAVA;
        settings.debugExporting = true;
        NeuralDebugger neuralDebugger = new NeuralDebugger(Runner.getSources(getDatasetArgs("simple/family"), settings), settings);
        neuralDebugger.executeDebug();
    }

    @TestAnnotations.Slow
    public void loadGroundSamples() throws Exception {
        List<NeuralSample> neuralSamples = new JavaExporter().importListFrom(Paths.get("../Resources/datasets/simple/family/mock/NeuralDebugger.java"), NeuralSample.class);
        for (NeuralSample neuralSample : neuralSamples) {
            System.out.println(neuralSample.exportToJson());
        }
    }
}