package cz.cvut.fel.ida.logging;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * We want immediate flushing into the output console
 */
public class FlushStreamHandler extends StreamHandler {
    private static final Logger LOG = Logger.getLogger(FlushStreamHandler.class.getName());

    public FlushStreamHandler(OutputStream out, Formatter f) {
        super(out, f);
    }

    @Override
    public synchronized void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    @Override
    public void close() throws SecurityException {
        flush();
    }
}
