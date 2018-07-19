import ida.utils.tuples.Pair;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import pipeline.Pipeline;
import pipeline.PipelineBuilder;
import settings.Settings;
import settings.Sources;
import training.results.Results;
import utils.CommandLineHandler;
import utils.logging.Logging;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        Logging logging = new Logging();
        try {
            logging.initialize();
        } catch (IOException ex) {
            LOG.severe("Could not initialize Logging\n" + ex.getMessage());
            System.exit(1);
        }

        CommandLineHandler cmdh = new CommandLineHandler();
        Settings settings = new Settings();
        Sources sources = null;
        try {
            CommandLine cmd = cmdh.parseParams(args, settings);
            settings.setupFromCommandline(cmd);
            sources = Sources.setupFromCommandline(settings, cmd);
        } catch (ParseException ex) {
            LOG.severe("Unable to parse Commandline arguments into settings/sources\n" + ex.getMessage());
            System.exit(1);
        }

        //place for external changes in setting object for non-standard pipeline

        Pair<Boolean, String> validation = settings.validate();
        if (!validation.r) {
            LOG.severe("Invalid pipeline setting.\n" + validation.s);
            System.exit(2);
        }
        validation = sources.isValid(settings);
        if (!validation.r) {
            LOG.severe("Invalid source files configuration.\n" + validation.s);
            System.exit(2);
        }

        PipelineBuilder pipelineBuilder = new PipelineBuilder(settings, sources);
        Pipeline<Sources, Results> pipeline = pipelineBuilder.buildPipeline();
        List<Pair<String, Results>> targets = pipeline.execute(sources);

        targets.forEach(stringResultsPair -> LOG.info(stringResultsPair.r + " : " + stringResultsPair.s));
        LOG.info("Pipeline " + pipeline + " has been successfully executed.");
    }
}