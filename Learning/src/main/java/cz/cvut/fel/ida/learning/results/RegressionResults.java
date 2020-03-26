package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.algebra.utils.MathUtils;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.crossvalidation.MeanStdResults;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gusta on 8.3.17.
 */
public class RegressionResults extends Results {

    public RegressionResults(List<Result> outputs, Settings aggregationFcn) {
        super(outputs, aggregationFcn);
    }

    protected RegressionResults(Value meanError) {
        super(meanError);
    }

    @Override
    public String toString() {
//        return Arrays.toString(evaluations.toArray());
        return super.toString();
    }

    @Override
    public boolean recalculate() {
        return false;
    }

    @Override
    public boolean betterThan(Results other) {
        return other.error.greaterThan(error);      //now fail sooner than here if no entailed examples! :) -> done, expections throwing propagation
    }

    public static MeanStdResults aggregateRegressions(List<RegressionResults> resultsList) {
        List<Value> errors = resultsList.stream().map(res -> res.error).collect(Collectors.toList());
        Value meanError = MathUtils.getMeanValue(errors);
        RegressionResults mean = new RegressionResults(meanError);
        Value stdError = MathUtils.getStd(errors, meanError);
        RegressionResults std = new RegressionResults(stdError);
        return new MeanStdResults(mean,std);
    }

    @Override
    public String toString(Settings settings) {
        return null;
    }
}