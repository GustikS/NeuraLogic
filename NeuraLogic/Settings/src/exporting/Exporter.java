package exporting;

import settings.Settings;

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

    String id;

    String suffix = "";

    boolean crossvalDetected;

    /**
     * A particular named exporter - will create a new file
     *
     * @param settings
     * @param id
     */
    public Exporter(Settings settings, String id) {
        if (settings == null) {
            return;
        }
        this.id = id;

        if (settings.blockExporting == Settings.BlockExporting.JSON) {
            this.suffix = ".json";
        }
        this.exportWriter = getWriter(settings.exportDir + "/" + id + suffix, true);
    }

    public static Exporter getFrom(String id, Settings settings) {
        if (settings == null)
            return null;
        for (String exportPipeline : settings.exportBlocks) {
            if (id.equals(exportPipeline)) {
                if (settings.blockExporting == Settings.BlockExporting.TEXT)
                    return new TextExporter(settings, id);
                else
                    return getExporter(settings, id);
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

    public void exportSettings(String exportToJson, String settingsExportFile) {
        LOG.info("Exporting settings to " + settingsExportFile);
        PrintWriter settingsWriter = getWriter(settingsExportFile, false);
        settingsWriter.println(exportToJson);
        settingsWriter.flush();
        settingsWriter.close();
    }

    public void exportSources(String exportToJson, String exportFile) {
        LOG.info("Exporting sources to " + exportFile);
        PrintWriter writer = getWriter(exportFile, false);
        writer.println(exportFile);
        writer.flush();
        writer.close();
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

    public static Exporter getExporter(Settings settings, String id) {
        if (settings.blockExporting == Settings.BlockExporting.JSON)
            return new JsonExporter(settings, id);
        else if (settings.blockExporting == Settings.BlockExporting.TEXT)
            return new TextExporter(settings, id);
        else
            return new JsonExporter(settings, id);
    }

}