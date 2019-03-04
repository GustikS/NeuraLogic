package utils.logging;

/**
 * Created by gusta on 8.3.17.
 * <p>
 * Initialize all loggers (in a hierarchy) through the ROOT logger, and append handlers (output file logging) with formatters on them.
 * Create all necessary output files and directories.
 */

import settings.Settings;

import java.io.IOException;
import java.util.logging.*;

public class Logging {
    private FileHandler loggingFile;
    private Formatter formatter;

    public void initialize() throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT [%4$s] (%2$s) : %5$s%6$s%n");
        System.setProperty("java.util.logging.ConsoleHandler.level", "FINEST");

        // get the global logger to configure it
        //Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        //get the ROOT logger (higher than global logger)
        Logger rootLogger = Logger.getLogger("");

        // suppress the logging output to the console

        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            handlers[0].setLevel(Settings.loggingLevel);
            handlers[0].setFormatter(new ColoredFormatter());
            if (Settings.supressConsoleOutput) {
                rootLogger.removeHandler(handlers[0]);
            }
        }

        rootLogger.setLevel(Settings.loggingLevel);

        if (!Settings.supressLogFileOutput) {
            if (Settings.htmlLogging) {
                loggingFile = new FileHandler("Logging.html");
                formatter = new HtmlFormatter();
            } else {
                loggingFile = new FileHandler("Logging.txt");
                // create a TXT formatter for the handler
                formatter = new SimpleFormatter();
            }
            loggingFile.setFormatter(formatter);
            rootLogger.addHandler(loggingFile);
        }
    }
}
