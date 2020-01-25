package utils.exporting;

import settings.Settings;
import settings.Sources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gusta on 27.2.18.
 * <p>
 * Exporting logs, scripts, measurements, settings and traces of experiments. To destinations in local and external files, possibly DBs.
 */
public class Exporter {

    private static final Logger LOG = Logger.getLogger(Exporter.class.getName());

    Settings settings;

    File exportFile;
    PrintWriter exportWriter;

    String id;

    String suffix = "";

    boolean crossvalDetected;

    public Exporter(Settings settings) {
        this.settings = settings;
    }

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
        this.settings = settings;

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
                    return new Exporter(settings, id);
            }
        }
        return null;
    }

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

    public void exportSettings(Settings settings) {
        LOG.info("Exporting settings to " + settings.settingsExportFile);
        PrintWriter settingsWriter = getWriter(settings.settingsExportFile, false);
        settingsWriter.println(settings.exportToJson());
        settingsWriter.flush();
        settingsWriter.close();
    }

    public void exportSources(Sources sources) {
        LOG.info("Exporting sources to " + settings.sourcesExportFile);
        PrintWriter writer = getWriter(settings.sourcesExportFile, false);
        writer.println(sources.exportToJson());
        writer.flush();
        writer.close();
    }

    public void resultsLine(String line) {
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

    public void export(Exportable t) {
        if (crossvalDetected){
            repairJsonCrossvalStart(exportFile);
        }
        resultsLine(t.exportToJson());
        if (crossvalDetected) {
            repairJsonCrossvalEnd(exportFile);
        }
    }

    private void repairJsonCrossvalStart(File file) {
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

    private void repairJsonCrossvalEnd(File file) {
        exportWriter.println("]\n");
        exportWriter.flush();
    }
}