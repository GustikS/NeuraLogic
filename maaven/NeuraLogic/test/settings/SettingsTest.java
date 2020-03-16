package settings;

import cz.cvut.fel.ida.pipelines.exporting.JsonExporter;
import org.junit.Test;

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