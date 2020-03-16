package cz.cvut.fel.ida.utils.exporting;

//import cz.cvut.fel.ida.settings.Settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;

public class JsonExporter extends Exporter {
    private static final Logger LOG = Logger.getLogger(JsonExporter.class.getName());

    public JsonExporter(String ExportDir, String id) {
        super(ExportDir, id, "JSON");
    }

    public void export(Exportable t) {
        if (crossvalDetected) {
            repairJsonCrossvalStart(exportFile);
        }
        exportLine(t.exportToJson());
        if (crossvalDetected) {
            repairJsonCrossvalEnd(exportFile);
        }
    }

    protected void repairJsonCrossvalStart(File file) {
        try {
            List<String> strings = Files.readAllLines(file.toPath());
            if (strings.isEmpty()) {
                return;
            }
            if (strings.get(0).contains("[")) {
                strings.set(strings.size() - 2, ",\n");
            } else {
                strings.add(0, "[\n");
                strings.add(",\n");
            }
            FileWriter fileWriter = new FileWriter(file, false);
            PrintWriter out = new PrintWriter(fileWriter);
            for (String string : strings) {
                out.println(string);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void repairJsonCrossvalEnd(File file) {
        exportWriter.println("]\n");
        exportWriter.flush();
    }
}
