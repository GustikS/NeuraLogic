package cz.cvut.fel.ida.neuralogic.revised.debugging;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.GroundingDebugger;
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

public class GroundingDebuggerTest {
    private static final Logger LOG = Logger.getLogger(GroundingDebuggerTest.class.getName());

    @TestAnnotations.Slow
    public void family() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.intermediateDebug = false;
        settings.exportType = JAVA;
        settings.debugExporting = true;
        GroundingDebugger groundingDebugger = new GroundingDebugger(Runner.getSources(getDatasetArgs("simple/family"), settings), settings);
        groundingDebugger.executeDebug();
        assertTrue(Paths.get(Logging.logFile.toString(), "export", "debug", GroundingDebugger.class.getSimpleName() + ".java").toFile().exists());
    }

    @TestAnnotations.Slow
    public void loadGroundSamples() throws Exception {
        List<GroundingSample> groundingSamples = new JavaExporter().importListFrom(Paths.get("../Resources/datasets/simple/family/mock/" + GroundingDebugger.class.getSimpleName() + ".java"), GroundingSample.class);
        for (GroundingSample groundingSample : groundingSamples) {
            LOG.fine(groundingSample.exportToJson());
            assertNotNull(groundingSample.groundingWrap.getGroundTemplate());
        }
    }

}