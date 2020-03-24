package cz.cvut.fel.ida.utils.exporting;

//import cz.cvut.fel.ida.settings.Settings;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gusta on 27.2.18.
 * <p>
 * Exporting logs, scripts, measurements, settings and traces of experiments. To destinations in local and external files, possibly DBs.
 */
public abstract class Exporter {

    private static final Logger LOG = Logger.getLogger(Exporter.class.getName());

    String exportDir;
    File exportFile;

    String type;

    String id;

    String suffix = "";


    protected Exporter(String exportDir, String id, String type) {
        this.exportDir = exportDir;
        this.type = type;
        this.id = id;
        this.suffix = "." + type.toLowerCase();
        exportFile = new File(Paths.get(exportDir, id) + suffix);
    }

    protected Exporter() {

    }

    public static Exporter getFrom(String exportDir, String id, String[] exportBlocks, String type) {
//        if (settings == null)
//            return null;
        for (String exportPipeline : exportBlocks) {
            if (id.equals(exportPipeline)) {
                return getExporter(exportDir, id, type);
            }
        }
        return null;
    }

    public abstract void export(Exportable iExportable);

    public abstract <I> List<I> importListFrom(Path path, Class<I> cls);

    public abstract <I> I importObjectFrom(Path path, Class<I> cls);

    public void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    public static Exporter getExporter(String exportDir, String id, String type) {
        if (type == "JSON")
            return new JsonExporter(exportDir, id);
        else if (type == "JAVA")
            return new JavaExporter(exportDir, id);
        else
            return new JsonExporter(exportDir, id);
    }

    public abstract void delimitNext();

    public abstract void delimitStart();

    public abstract void delimitEnd();

    public abstract void finish();
}