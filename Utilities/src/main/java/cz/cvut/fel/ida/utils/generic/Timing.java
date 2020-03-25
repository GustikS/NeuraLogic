package cz.cvut.fel.ida.utils.generic;

import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class Timing implements Exportable {
    private static final Logger LOG = Logger.getLogger(Timing.class.getName());

    private Duration timeTaken;
    transient Instant now;

    String totalTimeTaken;

    long allocatedMemory;

    public Timing() {
        setTimeTaken(Duration.ofMillis(0));
    }

    public void tic() {
        now = Instant.now();
    }

    public void toc() {
        Instant later = Instant.now();
        Duration elapsed = Duration.between(now, later);
        setTimeTaken(getTimeTaken().plus(elapsed));
        now = later;
    }

    public void checkMemory() {
        Utilities.logMemory();
        allocatedMemory = Utilities.allocatedMemory / Utilities.mb;
    }

    public void finish() {
        totalTimeTaken = getTimeTaken().toString();
        checkMemory();
    }

    public Duration getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Duration timeTaken) {
        this.timeTaken = timeTaken;
    }
}
