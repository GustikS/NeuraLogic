package cz.cvut.fel.ida.utils.exporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.logging.Logger;


public abstract class TextExporter extends Exporter {
    private static final Logger LOG = Logger.getLogger(TextExporter.class.getName());

    PrintWriter exportWriter;

    /**
     * Repeated export to the same file (e.g. Crossvalidation folds)
     */
    boolean repeatedExportAppend;

    /**
     * A particular named exporter - will create a new file
     *
     * @param id
     */
    public TextExporter(String exportDir, String id, String type) {
        super(exportDir, id, type);
        if (exportFile.exists()) {
            repeatedExportAppend = true;
        }
        if (!id.equals(""))
            this.exportWriter = getWriter(exportFile.toString(), true);
    }

    protected TextExporter() {
    }

    public void finish() {
        exportWriter.flush();
        exportWriter.close();
    }

    public void exportLine(String line) {
        exportWriter.println(line);
        exportWriter.println();
        exportWriter.flush();
    }

    private static PrintWriter getWriter(String filename, boolean append) {
        FileWriter fw = null;
        File file = new File(filename);
        try {
            createFile(file);
            fw = new FileWriter(filename, append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(fw);
        return out;
    }

    public static boolean createFile(File file) {
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        boolean created = false;// if file already exists will do nothing
        try {
            created = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return created;
    }

    public static void exportString(String exportToJson, Path exportFile) {
        LOG.info("Exporting serialized object to " + exportFile);
        PrintWriter settingsWriter = getWriter(exportFile.toString(), false);
        settingsWriter.println(exportToJson);
        settingsWriter.flush();
        settingsWriter.close();
    }
}
