package utils.logging;

import org.junit.Test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by gusta on 27.2.18.
 */
public class LoggerTests {

    @Test
    public void logToConsole() throws IOException {
        GLogger.setupConsoleLogger();
        Logger LOG = Logger.getLogger("consoleLogger");
        logMessages(LOG);
    }

    @Test
    public void logToFile() throws IOException {
        GLogger.setupFileLogger();
        Logger LOG = Logger.getLogger("fileLogger");
        logMessages(LOG);
    }

    public void logMessages(Logger LOG) {
        // set the LogLevel to Severe, only severe Messages will be written
        LOG.setLevel(Level.SEVERE);
        LOG.severe("Info Log");
        LOG.warning("Info Log");
        LOG.info("Info Log");
        LOG.finest("Really not important");

        // set the LogLevel to Info, severe, warning and info will be written
        // finest is still not written
        LOG.setLevel(Level.INFO);
        LOG.severe("Info Log");
        LOG.warning("Info Log");
        LOG.info("Info Log");
        LOG.finest("Really not important");
    }
}
