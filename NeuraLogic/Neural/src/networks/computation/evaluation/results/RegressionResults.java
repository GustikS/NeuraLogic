package networks.computation.evaluation.results;

import networks.computation.evaluation.functions.Aggregation;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class RegressionResults extends Results {

    public RegressionResults(List<Result> outputs, Aggregation aggregationFcn) {
        super(outputs, aggregationFcn);
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean recalculate() {
        return false;
    }

    @Override
    public boolean betterThan(Results other) {
        return other.error.greaterThan(error);
    }
}