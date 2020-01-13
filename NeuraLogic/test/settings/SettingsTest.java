package settings;

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
        settings.exporter.exportSettings(settings);
    }


    @Test
    public void importFromJson() {
        Settings settings = new Settings().loadFromJson("./resources/settings/settings.json");
        System.out.println(settings);
    }
}