package cz.cvut.fel.ida.old_tests.utils.logging;

import cz.cvut.fel.ida.logging.Logging;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by gusta on 27.2.18.
 */
public class LoggerTests {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Test
    public void BasicLoggerTest() {
        Logging gl;
        // set the LogLevel to Severe, only severe Messages will be written
        LOGGER.setLevel(Level.SEVERE);
        LOGGER.severe("Info Log");
        LOGGER.warning("Info Log");
        LOGGER.info("Info Log");
        LOGGER.finest("Really not important");

        // set the LogLevel to Info, severe, warning and info will be written
        // finest is still not written
        LOGGER.setLevel(Level.INFO);
        LOGGER.severe("Info Log");
        LOGGER.warning("Info Log");
        LOGGER.info("Info Log");
        LOGGER.finest("Really not important");
    }
}