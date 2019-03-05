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

    Formatter fileFormatter;
    Formatter consoleFormatter;

    public void initialize() throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT [%4$s] (%2$s) : %5$s%6$s%n");
        System.setProperty("java.util.logging.ConsoleHandler.level", "FINEST");

        // get the global logger to configure it
        //Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        //get the ROOT logger (higher than global logger)
        Logger rootLogger = Logger.getLogger("");

        // remove the default logging output to the console - it outputs everything to standard error output - not nice
        Handler[] handlers = rootLogger.getHandlers();
        Handler defaultConsoleHandler = handlers[0];
        if (defaultConsoleHandler instanceof ConsoleHandler) {
            rootLogger.removeHandler(defaultConsoleHandler);
            if (!Settings.supressConsoleOutput) {

                if (Settings.customLogColors) {
                    consoleFormatter = new ColoredFormatter();
                } else {
                    consoleFormatter = new SimpleFormatter();
                }
                StreamHandler sh = new StreamHandler(System.out, consoleFormatter);
                sh.setLevel(Settings.loggingLevel);
                rootLogger.addHandler(sh);
            }
        }

        rootLogger.setLevel(Settings.loggingLevel);

        if (!Settings.supressLogFileOutput) {

            if (Settings.htmlLogging) {
                loggingFile = new FileHandler("out/Logging.html");
                fileFormatter = new HtmlFormatter();
            } else {
                loggingFile = new FileHandler("out/Logging.txt");
                fileFormatter = new SimpleFormatter();
            }
            loggingFile.setFormatter(fileFormatter);
            rootLogger.addHandler(loggingFile);
        }
    }
}
