package utils;

import building.LearningSchemeBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;
import utils.generic.Pair;
import utils.logging.Logging;

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

        return Sources.getSources(cmd, settings);
    }
}
