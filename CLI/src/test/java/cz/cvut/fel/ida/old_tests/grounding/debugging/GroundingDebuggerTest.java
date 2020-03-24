package cz.cvut.fel.ida.old_tests.grounding.debugging;

import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.GroundingDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.JavaExporter;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.nio.file.Paths;
import java.util.List;

import static cz.cvut.fel.ida.setup.Settings.ExportFileType.JAVA;
import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;

public class GroundingDebuggerTest {

    @TestAnnotations.Slow
    public void family() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.intermediateDebug = false;
        settings.exportType = JAVA;
        settings.debugExporting = true;
        GroundingDebugger groundingDebugger = new GroundingDebugger(Runner.getSources(getDatasetArgs("simple/family"), settings), settings);
        groundingDebugger.executeDebug();
    }

    @TestAnnotations.Slow
    public void loadGroundSamples() throws Exception {
        List<GroundingSample> groundingSamples = new JavaExporter().importListFrom(Paths.get("../Resources/datasets/simple/family/mock/GroundingDebugger.java"), GroundingSample.class);
        for (GroundingSample groundingSample : groundingSamples) {
            System.out.println(groundingSample.exportToJson());
        }
    }

}