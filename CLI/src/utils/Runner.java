package utils;

import building.LearningSchemeBuilder;
import exporting.Exporter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;
import utils.generic.Pair;
import utils.logging.Logging;

import java.io.File;
import java.util.logging.Logger;

public class Runner {
    private static final Logger LOG = Logger.getLogger(Runner.class.getName());

    public static void main(String[] args) {
        main(args, new Settings());
    }

    public static void main(String[] args, Settings settings) {
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

    public static Sources getSources(String[] args, Settings settings) {
        CommandLineHandler cmdh = new CommandLineHandler();
        CommandLine cmd = null;
        try {
            cmd = cmdh.parseParams(args, settings);
        } catch (ParseException ex) {
            LOG.severe("Unable to parse Commandline arguments!" + ex);
            System.exit(1);
        }

        Sources sources = Sources.getSources(cmd, settings);

        startupExport(settings, sources);

        return sources;
    }

    private static void startupExport(Settings settings, Sources sources) {
        Exporter exporter = Exporter.getExporter(settings.exportDir, "", settings.blockExporting.name());

        if (settings.cleanUpFirst) {
            exporter.deleteDir(new File(settings.exportDir));
        }

        LOG.info("Exporting Settings");
        exporter.exportObject(settings.export(), settings.settingsExportFile);

        LOG.info("Exporting Sources");
        exporter.exportObject(sources.export(), settings.sourcesExportFile);
    }
}
