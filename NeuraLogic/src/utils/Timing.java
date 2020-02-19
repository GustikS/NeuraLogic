package utils;

import utils.exporting.Exportable;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class Timing implements Exportable {
    private static final Logger LOG = Logger.getLogger(Timing.class.getName());

    Duration timeTaken;
    transient Instant now;

    String totalTimeTaken;

    long allocatedMemory;

    public Timing() {
        timeTaken = Duration.ofMillis(0);
    }

    public void tic() {
        now = Instant.now();
    }

    public void toc() {
        Instant later = Instant.now();
        Duration elapsed = Duration.between(now, later);
        timeTaken = timeTaken.plus(elapsed);
        now = later;
    }

    public void checkMemory() {
        Utilities.logMemory();
        allocatedMemory = Utilities.allocatedMemory / Utilities.mb;
    }

    public void finish() {
        totalTimeTaken = timeTaken.toString();
        checkMemory();
    }
}
