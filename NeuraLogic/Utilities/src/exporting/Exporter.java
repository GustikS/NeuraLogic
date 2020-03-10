package exporting;

//import settings.Settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * Created by gusta on 27.2.18.
 * <p>
 * Exporting logs, scripts, measurements, settings and traces of experiments. To destinations in local and external files, possibly DBs.
 */
public abstract class Exporter {

    private static final Logger LOG = Logger.getLogger(Exporter.class.getName());

    File exportFile;
    PrintWriter exportWriter;

    String type;

    String id;

    String suffix = "";

    boolean crossvalDetected;

    /**
     * A particular named exporter - will create a new file
     *
     * @param id
     */
    public Exporter(String exportDir, String id, String type) {
//        if (settings == null) {
//            return;
//        }
        this(type, id);
        this.exportWriter = getWriter(exportDir + "/" + id + suffix, true);
    }

    private Exporter(String type, String id) {
        this.type = type;
        this.id = id;
        this.suffix = "." + type.toLowerCase();
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

    public abstract <I> void export(Exportable iExportable);


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

    public void exportObject(String exportToJson, String exportFile) {
        LOG.info("Exporting serialized object to " + exportFile);
        PrintWriter settingsWriter = getWriter(exportFile + suffix, false);
        settingsWriter.println(exportToJson);
        settingsWriter.flush();
        settingsWriter.close();
    }

    public void exportLine(String line) {
        exportWriter.println(line);
        exportWriter.println();
        exportWriter.flush();
    }

    private PrintWriter getWriter(String filename, boolean append) {
        FileWriter fw = null;
        try {
            exportFile = new File(filename);
            if (exportFile.getParentFile() != null)
                exportFile.getParentFile().mkdirs();
            if (exportFile.exists() && append) {
                crossvalDetected = true;
            }
            exportFile.createNewFile(); // if file already exists will do nothing
            fw = new FileWriter(filename, append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(fw);
        return out;
    }

    public static Exporter getExporter(String exportDir, String id, String type) {
        if (type == "JSON")
            return new JsonExporter(exportDir, id);
        else if (type == "TEXT")
            return new TextExporter(exportDir, id);
        else
            return new JsonExporter(exportDir, id);
    }

}