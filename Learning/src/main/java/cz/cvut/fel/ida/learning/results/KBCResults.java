package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.learning.results.metrics.HITS;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.logging.Logger;

public class KBCResults extends DetailedClassificationResults {
    private static final Logger LOG = Logger.getLogger(KBCResults.class.getName());

    HITS hits;
    HITS.Stats kbc;

    public KBCResults(List<Result> outputs, Settings aggregationFcn, HITS hits) {
        super(outputs, aggregationFcn);
        this.hits = hits;
    }

    @Override
    public boolean recalculate() {
        LOG.info("(P)recalculation of HITs database + statistics...");
        super.recalculate();
        if (hits == null)
            hits = new HITS(evaluations, settings);
        kbc = hits.getStats(evaluations);
        LOG.info("...HITs calculated.");
        return true;
    }

    @Override
    public String toString(Settings settings) {
        String s = super.toString(settings);
        StringBuilder sb = new StringBuilder(s);
        if (kbc != null) {
            sb.append(", ").append(kbc.toString());
        }
        return sb.toString();
    }
}
