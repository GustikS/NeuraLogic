import networks.computation.evaluation.results.Results;
import org.apache.commons.cli.CommandLine;
import pipelines.Pipeline;
import pipelines.building.AbstractPipelineBuilder;
import settings.Settings;
import settings.Sources;
import utils.CommandLineHandler;
import utils.generic.Pair;
import utils.logging.Logging;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        main(args, new Settings());
    }

    public static void main(String[] args, Settings settings) {

        Logging logging = initLogging();

        if (settings == null) {
            settings = new Settings();
        }

        Sources sources = getSources(args, settings);
        Pipeline<Sources, Results> pipeline = getPipeline(settings, sources);
        settings.root = pipeline;

        LOG.finest("Running the main pipeline on the provided sources...");
        Pair<String, Results> target = pipeline.execute(sources);
        LOG.info("Pipeline: " + target.r + " finished with result: " + target.s.toString());

        logging.finish();
    }

    public static Logging initLogging() {
        Logging logging = new Logging();
        try {
            logging.initialize();
            LOG.info("Launched NeuraLogic from location " + System.getProperty("user.dir"));
        } catch (IOException ex) {
            LOG.severe("Could not initialize Logging.\n" + ex.getMessage());
            System.exit(1);
        }
        return logging;
    }

    public static Pipeline<Sources, Results> getPipeline(Settings settings, Sources sources) {
        LOG.finest("Building the main pipeline...");
        AbstractPipelineBuilder<Sources, Results> pipelineBuilder = AbstractPipelineBuilder.getBuilder(sources, settings);
        Pipeline<Sources, Results> pipeline = pipelineBuilder.buildPipeline();
        LOG.finest("The main pipeline has been built");
        return pipeline;
    }

    public static Sources getSources(String[] args, Settings settings) {
        CommandLineHandler cmdh = new CommandLineHandler();
        Sources sources = null;
        try {
            CommandLine cmd = cmdh.parseParams(args, settings);
            settings.setupFromCommandline(cmd);
            LOG.info("Settings loaded and set up.");
            sources = Sources.setupFromCommandline(settings, cmd);
            LOG.info("Sources loaded and set up.");
        } catch (Exception ex) {
            LOG.severe("Unable to parse Commandline arguments into settings/source files.\n" + ex);
            System.exit(1);
        }

        settings.infer();
        Pair<Boolean, String> validation = settings.validate();
        if (!validation.r) {
            LOG.severe("Invalid pipelines setting.\n" + validation.s);
            System.exit(2);
        }
        validation = sources.validate(settings);
        if (!validation.r) {
            LOG.severe("Invalid source files configuration.\n" + validation.s);
            System.exit(2);
        }
        return sources;
    }

    public static String testConnection(String msg) {
        return msg + " succesfully connected";
    }
}