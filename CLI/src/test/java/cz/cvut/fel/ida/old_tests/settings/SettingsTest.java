package cz.cvut.fel.ida.old_tests.settings;

import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.JsonExporter;
import org.junit.jupiter.api.Test;

public class SettingsTest {

    @Test
    public void exportToJson() {
        Settings settings = new Settings();
        String json = settings.exportToJson();
        System.out.println(json);
    }

    @Test
    public void exportToJsonFile() {
        Settings settings = new Settings();
        new JsonExporter(settings.exportDir,"").exportObject(settings.exportToJson(), settings.settingsExportFile);
    }


    @Test
    public void importFromJson() {
        Settings settings = new Settings().loadFromJson("./resources/settings/settings.json");
        System.out.println(settings);
    }
}