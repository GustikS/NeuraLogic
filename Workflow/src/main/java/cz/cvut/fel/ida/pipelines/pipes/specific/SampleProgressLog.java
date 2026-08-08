package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.setup.Settings;

import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Narrating every sample is what made a single run produce a 17 MB log, but the lines themselves are worth
 * having - so keep them for the first few samples, then switch to a running total.
 * <p>
 * A running total rather than only a summary at the end, because the end is not reliable: the
 * <code>onClose</code> hook the pipes already use fires in the CLI - 52 times over the Java test suite - and
 * never through PyNeuraLogic, which does not close the stream. {@link #summary()} is called from it where it
 * does fire, and elsewhere the last periodic line stands in. The periodic line is also what a run over 100k
 * samples most wants anyway: some sign that it is progressing.
 */
public class SampleProgressLog {
    private final Logger log;
    private final String stage;
    private final int detailLimit;
    private final int interval;
    private final String[] names;
    private final long[] totals;

    private final long startedAt = System.nanoTime();
    private long samples;
    private boolean summarized;

    public SampleProgressLog(Logger log, Settings settings, String stage, String... metricNames) {
        this.log = log;
        this.stage = stage;
        this.detailLimit = settings.loggedSampleDetails;
        this.interval = settings.sampleLogInterval;
        this.names = metricNames;
        this.totals = new long[metricNames.length];
    }

    /**
     * @param detail  the per-sample line, only built while it is still going to be printed
     * @param metrics one value per name given to the constructor, in the same order
     */
    public void sample(Supplier<String> detail, long... metrics) {
        samples++;
        summarized = false;     //a new sample makes another summary due, so a second dataset is not left silent
        for (int i = 0; i < totals.length && i < metrics.length; i++) {
            totals[i] += metrics[i];
        }

        if (detailLimit > 0 && samples <= detailLimit) {
            log.info(stage + " of sample " + samples + ": " + detail.get());
            if (samples == detailLimit) {
                log.info(stage + ": further samples are summarised every " + interval
                        + " rather than listed (loggedSampleDetails)");
            }
        } else if (interval > 0 && samples % interval == 0) {
            log.info(progress());
        }
    }

    /**
     * Only says anything if there were samples this run and nobody has summarised them yet, so it is safe to
     * call from an <code>onClose</code> that may or may not fire.
     */
    public void summary() {
        if (samples == 0 || summarized) {
            return;
        }
        summarized = true;
        log.info(progress());
    }

    private String progress() {
        StringBuilder sb = new StringBuilder(stage).append(": ").append(samples).append(" samples");
        for (int i = 0; i < names.length; i++) {
            sb.append(", ").append(names[i]).append(' ').append(totals[i])
                    .append(" (").append(String.format("%.1f", (double) totals[i] / samples)).append("/sample)");
        }
        double seconds = (System.nanoTime() - startedAt) / 1_000_000_000.0;
        return sb.append(", ").append(String.format("%.1f", seconds)).append("s").toString();
    }
}
