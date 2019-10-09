package utils;

import settings.Settings;

import java.io.*;

/**
 * Created by gusta on 27.2.18.
 * <p>
 * Exporting logs, scripts, measurements, settings and traces of experiments. To destinations in local and external files, possibly DBs.
 */
public class Exporter {

    Settings settings;

    PrintWriter tmpWriter;
    PrintWriter resultsWriter;

    public Exporter(Settings settings) {
        this.settings = settings;
        this.tmpWriter = getWriter(settings.tmpFile);
        this.resultsWriter = getWriter(settings.resultFile);
    }

    public void tmpLine(String line) {
        tmpWriter.println(line);
        tmpWriter.flush();
    }

    public void resultsLine(String line) {
        resultsWriter.println(line);
        tmpWriter.flush();
    }

    private PrintWriter getWriter(String filename) {
        FileWriter fw = null;
        try {
            File file = new File(filename);
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
            file.createNewFile(); // if file already exists will do nothing
            fw = new FileWriter(filename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(fw);
        return out;
    }
}