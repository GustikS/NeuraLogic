package cz.cvut.fel.ida.neuralogic.revised.debugging;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.debugging.TemplateDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.nio.file.Paths;

import static cz.cvut.fel.ida.setup.Settings.ExportFileType.JAVA;
import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateDebuggerTest {

    @TestAnnotations.Fast
    public void family() throws Exception {
        Settings settings = Settings.forSlowTest();
        settings.intermediateDebug = false;
        settings.exportType = JAVA;
        settings.debugExporting = true;

        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);

        TemplateDebugger templateDebugger = new TemplateDebugger(sources, settings);
        templateDebugger.executeDebug();
        assertTrue(Paths.get(Logging.logFile.toString(), "export", "debug", TemplateDebugger.class.getSimpleName() + ".java").toFile().exists());
    }

    @TestAnnotations.Interactive
    public void drawFamily() throws Exception {
        Settings settings = Settings.forInteractiveTest();
        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        TemplateDebugger templateDebugger = new TemplateDebugger(sources, settings);
        templateDebugger.executeDebug();
    }
}