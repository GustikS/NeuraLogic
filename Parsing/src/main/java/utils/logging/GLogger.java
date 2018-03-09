package utils.logging;

/**
 * Created by gusta on 8.3.17.
 *
 * Initialize all loggers (in a hierarchy) and append handlers and formatters on them
 * Create all necessary output files and directories
 */
import java.io.IOException;
import java.util.logging.*;

public class GLogger {
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;

    static public void setupFileLogger() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger("fileLogger");

        logger.setLevel(Level.FINEST);
        fileTxt = new FileHandler("Logging.%u.%g.txt", 1024*1024, 10);

        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

    }

    static public void setupConsoleLogger() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger("consoleLogger");

        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        //logger.addHandler(new ConsoleHandler());

    }
}
