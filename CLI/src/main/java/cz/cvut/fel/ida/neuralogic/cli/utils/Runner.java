package cz.cvut.fel.ida.neuralogic.cli.utils;

import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.building.LearningSchemeBuilder;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.exporting.Exporter;
import cz.cvut.fel.ida.utils.exporting.TextExporter;
import cz.cvut.fel.ida.utils.generic.Pair;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Utilities.myExit;

public class Runner {
    private static final Logger LOG = Logger.getLogger(Runner.class.getName());

    public static void main(String[] args) throws Exception {
        main(args, new Settings());
    }

    public static void main(String[] args, Settings settings) throws Exception {
        Logging logging = Logging.initLogging();

        if (settings == null) {
            settings = new Settings();
        }
        Sources sources = getSources(args, settings);

        Pipeline<Sources, ?> pipeline = LearningSchemeBuilder.getPipeline(settings, sources);
//        settings.root = pipeline;

        LOG.finest("Running the main pipeline on the provided sources...");
        Pair<String, ?> target = pipeline.execute(sources);
        LOG.info("Pipeline: " + target.r + " finished with result: " + target.s.toString());

        logging.finish();
    }

    public static Sources getSources(String[] args, Settings settings) throws Exception {
        CommandLineHandler cmdh = new CommandLineHandler();
        CommandLine cmd = null;
        try {
            cmd = cmdh.parseParams(args, settings);
        } catch (ParseException ex) {
            myExit(LOG, "Unable to parse Commandline arguments!" + ex);
        }

        Sources sources = Sources.getSources(cmd, settings);

        startupExport(settings, sources);

        return sources;
    }

    private static void startupExport(Settings settings, Sources sources) {
        Exporter exporter = Exporter.getExporter(settings.exportDir, "", settings.exportType.name());

        if (settings.cleanUpFirst) {
            exporter.deleteDir(new File(settings.exportDir));
        }

        LOG.info("Exporting Settings configuration:");
        TextExporter.exportString(settings.export(), Paths.get(settings.settingsExportFile));

        LOG.info("Exporting Sources configuration:");
        TextExporter.exportString(sources.export(), Paths.get(settings.sourcesExportFile));
    }

    public static String testConnection(String socket) {
        return socket + " succesfully connected";
    }
}
