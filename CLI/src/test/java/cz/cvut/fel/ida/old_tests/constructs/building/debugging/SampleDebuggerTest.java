package cz.cvut.fel.ida.old_tests.constructs.building.debugging;

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
    }

    @TestAnnotations.Slow
    public void loadGroundSamples() throws Exception {
        List<LogicSample> logicSamples = new JavaExporter().importListFrom(Paths.get("../Resources/datasets/simple/family/mock/SampleDebugger.java"), LogicSample.class);
        for (LogicSample logicSample : logicSamples) {
            LOG.fine(logicSample.exportToJson());
//            LOG.warning(logicSample.query.evidence.exportToJson());
        }
    }
}
