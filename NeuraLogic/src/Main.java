import networks.computation.evaluation.results.Results;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;
import utils.generic.Pair;
import utils.logging.Logging;

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

        Logging logging = Logging.initLogging();

        if (settings == null) {
            settings = new Settings();
        }

        Sources sources = Sources.getSources(args, settings);
        Pipeline<Sources, Results> pipeline = Pipeline.getPipeline(settings, sources);
        settings.root = pipeline;

        LOG.finest("Running the main pipeline on the provided sources...");
        Pair<String, Results> target = pipeline.execute(sources);
        LOG.info("Pipeline: " + target.r + " finished with result: " + target.s.toString());

        logging.finish();
    }

    public static String testConnection(String msg) {
        return msg + " succesfully connected";
    }
}