package cz.cvut.fel.ida.utils.exporting;

//import cz.cvut.fel.ida.settings.Settings;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class JsonExporter extends TextExporter {
    private static final Logger LOG = Logger.getLogger(JsonExporter.class.getName());

    public JsonExporter(String ExportDir, String id) {
        super(ExportDir, id, "JSON");
    }

    public JsonExporter() {
        super();
    }

    public void export(Exportable t) {
        if (repeatedExportAppend) {
            repairJsonCrossvalStart(exportFile);
        }
        exportLine(t.exportToJson());
        if (repeatedExportAppend) {
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

    @Override
    public <I> List<I> importListFrom(Path path, Class<I> cls) {
        //        Type listType = new TypeToken<ArrayList<S>>(){}.getType();
        Class<?> aClass = Array.newInstance(cls, 1).getClass();
        try {
            String jsonArray = new String(Files.readAllBytes(path));
            List<I> yourClassList = new Gson().fromJson(jsonArray, (Type) aClass);
            return yourClassList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <I> I importObjectFrom(Path path, Class<I> cls) {
        try {
            String json = new String(Files.readAllBytes(path));
            I i = new Gson().fromJson(json, cls);
            return i;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delimitStart() {
        exportWriter.println("[");
    }

    @Override
    public void delimitNext() {
        exportWriter.println(",");
        exportWriter.flush();
    }

    @Override
    public void delimitEnd() {
        exportWriter.println("{}\n]");
    }
}
