package cz.cvut.fel.ida.neuralogic.revised.debugging;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.debugging.TrainingDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.JavaExporter;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.nio.file.Paths;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.setup.Settings.ExportFileType.JAVA;
import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainingDebuggerTest {
    private static final Logger LOG = Logger.getLogger(TrainingDebuggerTest.class.getName());

    @TestAnnotations.Fast
    public void family() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.intermediateDebug = false;
        settings.exportType = JAVA;
        settings.debugExporting = true;

        TrainingDebugger trainingDebugger = new TrainingDebugger(Runner.getSources(getDatasetArgs("simple/family"), settings), settings);
        trainingDebugger.executeDebug();
        assertTrue(Paths.get(Logging.logFile.toString(), "export", "debug", TrainingDebugger.class.getSimpleName() + ".java").toFile().exists());
    }

    @TestAnnotations.Fast
    public void loadGroundSamples() throws Exception {
        family();
        Template template = new JavaExporter().importObjectFrom(Paths.get(Logging.logFile.toString(), "export", "debug", TrainingDebugger.class.getSimpleName() + ".java"), Template.class);
        LOG.fine(template.exportToJson());
        LOG.finer(template.toString());
        assertNotNull(template.rules);
    }

    @TestAnnotations.Interactive
    public void drawing() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        settings.maxCumEpochCount = 1;
        settings.isoValueCompression = false;
        settings.chainPruning = false;
        settings.debugAll = true;
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs("debug/drawing/noquery"), settings);
    }

    @TestAnnotations.Interactive
    public void drawingCompact() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        settings.maxCumEpochCount = 1;
        settings.isoValueCompression = false;
        settings.chainPruning = false;
        settings.debugAll = true;
        settings.drawingDetail = Settings.Detail.LOW;
        Pair<Pipeline, ?> results = Main.main(getDatasetArgs("simple/visual", "-t ./template2.txt"), settings);
    }
}