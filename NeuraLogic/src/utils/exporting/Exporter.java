package utils.exporting;

import learning.crossvalidation.TrainTestResults;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Results;
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
public class Exporter {

    private static final Logger LOG = Logger.getLogger(Exporter.class.getName());

    Settings settings;

    PrintWriter settingsWriter;
    PrintWriter resultsWriter;

    String id;

    public Exporter(Settings settings) {
        this.settings = settings;
        this.settingsWriter = getWriter(settings.settingsExportFile, false);
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
        this.resultsWriter = getWriter(settings.exportDir + "/" + id, true);
    }

    public static Exporter getFrom(String id, Settings settings) {
        if (settings == null)
            return null;
        for (String exportPipeline : settings.exportBlocks) {
            if (id.equals(exportPipeline)) {
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
        settingsWriter.println(settings.exportToJson());
        settingsWriter.flush();
        settingsWriter.close();
    }

    public void resultsLine(String line) {
        resultsWriter.println(line);
        resultsWriter.flush();
    }


    private PrintWriter getWriter(String filename, boolean append) {
        FileWriter fw = null;
        try {
            File file = new File(filename);
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
            file.createNewFile(); // if file already exists will do nothing
            fw = new FileWriter(filename, append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(fw);
        return out;
    }

    public <T> void export(T t) {
        resultsLine(t.toString());
    }

    public void export(TrainTestResults trainTestResults) {
        resultsLine("TrainTestResults:");
        trainTestResults.training.bestResults.export(this);
        resultsLine("Testing:");
        trainTestResults.testing.export(this);
    }

    public void export(Results results) {
//        resultsLine("Results:");
        resultsLine(results.toString(settings));
    }

    public void export(Progress.TrainVal trainVal) {
        resultsLine("TrainVal:");
        resultsLine("Training:");
        trainVal.training.export(this);
        resultsLine("Validation:");
        trainVal.validation.export(this);
    }

    public void export(Progress progress) {
        resultsLine("Progress:");
        resultsLine("BestResults:");
        progress.bestResults.export(this);
        resultsLine("Training");
        for (Progress.Restart restart : progress.restarts) {
            resultsLine("onlineTrainingResults-----------------");
            for (Results onlineTrainingResult : restart.onlineTrainingResults) {
                export(onlineTrainingResult);
            }
            resultsLine("trueTrainingResults----------------");
            for (Results trueTrainingResult : restart.trueTrainingResults) {
                export(trueTrainingResult);
            }
            resultsLine("validationResults------------------");
            for (Results validationResult : restart.validationResults) {
                export(validationResult);
            }
        }
    }

}