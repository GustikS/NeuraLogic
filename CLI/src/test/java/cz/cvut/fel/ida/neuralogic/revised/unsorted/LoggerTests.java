package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by gusta on 27.2.18.
 */
public class LoggerTests {
    private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @TestAnnotations.Fast
    public void BasicLoggerTest() {
        // set the LogLevel to Severe, only severe Messages will be written
        LOG.setLevel(Level.SEVERE);
        LOG.severe("Severe Log");
        LOG.warning("Warning Log");
        LOG.info("Info Log");
        LOG.finest("Really not important");

        // set the LogLevel to Info, severe, warning and info will be written
        // finest is still not written
        LOG.setLevel(Level.INFO);
        LOG.severe("Severe Log");
        LOG.warning("Warning Log");
        LOG.info("Info Log");
        LOG.finest("Really not important");
    }
}