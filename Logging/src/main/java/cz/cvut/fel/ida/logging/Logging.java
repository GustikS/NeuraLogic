package cz.cvut.fel.ida.logging;


import cz.cvut.fel.ida.setup.Settings;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.*;

/**
 * Created by gusta on 8.3.17.
 * <p>
 * Initialize all loggers (in a hierarchy) through the ROOT logger, and append handlers (output file logging) with formatters on them.
 * Create all necessary output files and directories.
 */
public class Logging {
    private static final Logger LOG = Logger.getLogger(Logging.class.getName());

    private FileHandler loggingFile;

    Formatter fileFormatter;
    Formatter consoleFormatter;

    long startupTime = System.currentTimeMillis();

    public static Logging initLogging() throws Exception {
        return initLogging(Settings.loggingLevel);
    }

    public static Logging initLogging(Level loggingLevel) throws Exception {
        Logging logging = new Logging();
        try {
            logging.initialize(loggingLevel);
            LOG.info("Launched NeuraLogic from location " + System.getProperty("user.dir"));
        } catch (IOException ex) {
            throw new Exception("Could not initialize Logging.\n" + ex.getMessage());
//            System.exit(1);
        }
        return logging;
    }

    public void initialize(Level loggingLevel) throws IOException {
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
                StreamHandler sh = new FlushStreamHandler(System.out, consoleFormatter);
                sh.setLevel(loggingLevel);
                rootLogger.addHandler(sh);
            }
        }

        rootLogger.setLevel(loggingLevel);

        if (!Settings.supressLogFileOutput) {
            File parentFile = new File(Settings.logFile).getParentFile();
            if (parentFile != null)
                parentFile.mkdirs();
            if (Settings.htmlLogging) {
                loggingFile = new FileHandler(Settings.logFile + ".html");
                fileFormatter = new HtmlFormatter();
            } else {
                loggingFile = new FileHandler(Settings.logFile + ".txt");
                fileFormatter = new SimpleFormatter();
            }
            loggingFile.setFormatter(fileFormatter);
            rootLogger.addHandler(loggingFile);
        }
    }

    public void finish() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.info("Total time: " + calcDate(System.currentTimeMillis() - startupTime));
    }


    public static String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss:SS");
        date_format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }
}
