package utils.logging;

/**
 * Created by gusta on 8.3.17.
 *
 * Initialize all loggers (in a hierarchy) and append handlers and formatters on them
 * Create all necessary output files and directories
 */
import java.io.IOException;
import java.util.logging.*;

public class Logging {
     private FileHandler fileTxt;
     private SimpleFormatter formatterTxt;

    public void initialize() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        logger.setLevel(Level.FINEST);
        fileTxt = new FileHandler("Logging.txt");

        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

    }
}
