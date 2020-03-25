package cz.cvut.fel.ida.neuralogic.revised.debugging;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.SampleDebugger;
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

public class SampleDebuggerTest {
    private static final Logger LOG = Logger.getLogger(SampleDebuggerTest.class.getName());

    @TestAnnotations.Slow
    public void family() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.intermediateDebug = false;
        settings.exportType = JAVA;
        settings.debugExporting = true;
        SampleDebugger sampleDebugger = new SampleDebugger(Runner.getSources(getDatasetArgs("simple/family"), settings), settings);
        sampleDebugger.executeDebug();
        assertTrue(Paths.get(Logging.logFile.toString(), "export", "debug", SampleDebugger.class.getSimpleName() + ".java").toFile().exists());
    }

    @TestAnnotations.Slow
    public void loadGroundSamples() throws Exception {
        List<LogicSample> logicSamples = new JavaExporter().importListFrom(Paths.get("../Resources/datasets/simple/family/mock/" + SampleDebugger.class.getSimpleName() + ".java"), LogicSample.class);
        for (LogicSample logicSample : logicSamples) {
            LOG.fine(logicSample.exportToJson());
            assertNotNull(logicSample.query.evidence);
        }
    }
}
