import networks.computation.evaluation.results.Results;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import pipelines.Pipeline;
import pipelines.building.LearningSchemeBuilder;
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
        main(args, null);
    }

    public static void main(String[] args, Settings settings) {

        Logging logging = new Logging();
        try {
            logging.initialize();
        } catch (IOException ex) {
            LOG.severe("Could not initialize Logging.\n" + ex.getMessage());
            System.exit(1);
        }

        CommandLineHandler cmdh = new CommandLineHandler();
        if (settings == null) {
            settings = new Settings();
        }
        Sources sources = null;
        try {
            CommandLine cmd = cmdh.parseParams(args, settings);
            settings.setupFromCommandline(cmd);
            sources = Sources.setupFromCommandline(settings, cmd);
        } catch (ParseException ex) {
            LOG.severe("Unable to parse Commandline arguments into settings/source files.\n" + ex.getMessage());
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
        LOG.finest("Building LearningScheme pipeline...");
        LearningSchemeBuilder pipelineBuilder = new LearningSchemeBuilder(settings, sources);
        Pipeline<Sources, Results> pipeline = pipelineBuilder.buildPipeline();
        LOG.finest("LearningScheme pipeline has been built");
        LOG.finest("Running LearningScheme pipeline...");
        Pair<String, Results> target = pipeline.execute(sources);
        LOG.info("Pipeline: " + target.r + " finished with result: " + target.s);
        logging.finish();
    }
}