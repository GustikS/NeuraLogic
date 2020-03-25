package cz.cvut.fel.ida.neuralogic.revised.settings;

import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.SourceFiles;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.exporting.JsonExporter;
import cz.cvut.fel.ida.utils.exporting.TextExporter;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.nio.file.Path;
import java.nio.file.Paths;

import static cz.cvut.fel.ida.utils.generic.Utilities.getDatasetArgs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SourcesTest {

    @TestAnnotations.Fast
    public void exportToJson() throws Exception {
        Settings settings = Settings.forFastTest();
        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        String json = sources.exportToJson();
        System.out.println(json);
        assertFalse(json.isEmpty());
    }

    @TestAnnotations.Fast
    public void exportToJsonFile() throws Exception {
        Settings settings = new Settings();
        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        Path exportPath = Paths.get(Settings.logFile, settings.sourcesExportFile);
        TextExporter.exportString(sources.exportToJson(), exportPath);
        assertTrue(exportPath.toFile().exists());
    }

    @TestAnnotations.Fast
    public void exportImportFromJson() throws Exception {
        Settings settings = Settings.forFastTest();
        Sources sources = Runner.getSources(getDatasetArgs("simple/family"), settings);
        Path exportPath = Paths.get(Settings.logFile, settings.sourcesExportFile);
        TextExporter.exportString(sources.exportToJson(), exportPath);
        SourceFiles sourceFiles = new JsonExporter().importObjectFrom(exportPath, SourceFiles.class);
        assertTrue(sourceFiles.template.exists());
    }
}