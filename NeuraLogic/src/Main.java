import ida.utils.tuples.Pair;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import pipelines.Pipeline;
import settings.Settings;
import utils.CommandLineHandler;
import utils.logging.Logging;

import java.io.IOException;
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
        try {
            CommandLine cmd = cmdh.parseParams(args);
            settings.setupFromCommandline(cmd);
        } catch (ParseException ex) {
            LOG.severe("Unable to parse Commandline arguments into settings\n" + ex.getMessage());
            System.exit(1);
        }

        //place for external changes in setting object for non-standard pipelines
        Pair<Boolean, String> validation = settings.validate();
        if (!validation.r) {
            LOG.severe("Invalid pipeline setting, unable to run due to: " + validation.s);
            System.exit(2);
        }

        Pipeline pipeline = Pipeline.buildFrom(settings);
        pipeline.execute(settings);
    }
}
