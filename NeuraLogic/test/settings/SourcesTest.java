package settings;

import exporting.JsonExporter;
import org.junit.Test;
import utils.Runner;

public class SourcesTest {

    @Test
    public void exportToJson() {
        String[] args = ("-q ./resources/datasets/relational/kbs/nations/queries_small.txt " +
                "-e ./resources/datasets/relational/kbs/nations/embeddings " +
                "-t ./resources/datasets/relational/kbs/nations/template_2layers").split(" ");
        Settings settings = new Settings();
        Sources sources = Runner.getSources(args, settings);
        String json = sources.exportToJson();
        System.out.println(json);
    }

    @Test
    public void exportToJsonFile() {
        Settings settings = new Settings();
        new JsonExporter(settings.exportDir,"").exportObject(settings.exportToJson(), settings.settingsExportFile);
    }

    @Test
    public void importFromJson() {
        Settings settings = new Settings();
        SourceFiles sources = new SourceFiles(settings).loadFromJson("./resources/settings/sources.json");
        System.out.println(sources);
    }

}