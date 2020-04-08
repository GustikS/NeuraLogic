package cz.cvut.fel.ida.neuralogic.cli;

import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * Main runner for user mode, kills all exceptions without stacktrace
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        try {
            mainExc(args);
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\n");
        }
    }

    /**
     * Internal mode with exception stack traces
     *
     * @param args
     * @throws Exception
     */
    public static void mainExc(String[] args) throws Exception {
        main(args, new Settings());
    }

    
    public static Pair<Pipeline, ?> main(String[] args, Settings settings) throws Exception {
        return Runner.main(args, settings);
    }

}