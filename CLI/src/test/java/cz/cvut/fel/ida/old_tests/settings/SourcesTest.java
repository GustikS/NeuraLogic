package cz.cvut.fel.ida.old_tests.settings;

import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.SourceFiles;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.exporting.JsonExporter;
import org.junit.Test;

public class SourcesTest {

    @Test
    public void exportToJson() throws Exception {
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