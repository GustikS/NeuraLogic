package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.logging.Logger;

public class VoidResults extends Results {
    private static final Logger LOG = Logger.getLogger(VoidResults.class.getName());

    public VoidResults(List<Result> evaluations, Settings settings) {
        super(evaluations, settings);
    }

    @Override
    public boolean recalculate() {
        return true;
    }

    @Override
    public boolean betterThan(Results other, Settings.ModelSelection criterion) {
        return false;
    }

    @Override
    public String toString(Settings settings) {
        return "No target labels provided";
    }

    @Override
    public String toString() {
        return "No target labels provided";
    }
}
