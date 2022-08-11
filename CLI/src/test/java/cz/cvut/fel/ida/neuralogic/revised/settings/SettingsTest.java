package cz.cvut.fel.ida.neuralogic.revised.settings;

import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.TextExporter;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.junit.jupiter.api.Assertions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SettingsTest {

    private static final Logger LOG = Logger.getLogger(SettingsTest.class.getName());

    @TestAnnotations.Fast
    public void exportToJson() {
        Settings settings = new Settings();
        settings.infer();
        String json = settings.exportToJson();
        LOG.fine(json);
        assertFalse(json.isEmpty());
    }

    @TestAnnotations.Fast
    public void exportToJsonFile() {
        Settings settings = Settings.forFastTest();
        settings.infer();
        Path exportPath = Paths.get(Settings.logFile, settings.settingsExportFile);
        TextExporter.exportString(settings.exportToJson(), exportPath);
        assertTrue(exportPath.toFile().exists());
    }

    @TestAnnotations.Fast
    public void exportImportFromJson() {
        Settings settings = Settings.forFastTest();
        settings.infer();
        Path exportPath = Paths.get(Settings.logFile, settings.settingsExportFile);
        settings.maxCumEpochCount = 7;
        TextExporter.exportString(settings.exportToJson(), exportPath);
        settings.maxCumEpochCount = 10;
        settings = settings.updateFromJson(exportPath);
        LOG.fine(settings.export());
        Assertions.assertEquals(settings.maxCumEpochCount, 7);
    }
}