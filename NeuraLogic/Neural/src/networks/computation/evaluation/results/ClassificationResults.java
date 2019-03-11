package networks.computation.evaluation.results;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class ClassificationResults extends RegressionResults {
    private Double precision;
    private Double recall;
    private Value error;
    private Double f_Measure;
    private Double majorityErr;

    public ClassificationResults(List<Result> outputs, Aggregation aggregationFcn) {
        super(outputs, aggregationFcn);
    }

    @Override
    public boolean recalculate() {
        List<Value> errors = new ArrayList<>(evaluations.size());
        for (Result evaluation : evaluations) {
            errors.add(evaluation.errorValue());
        }
        error = aggregationFcn.evaluate(errors);
        return false;   //todo next rest
    }

    @Override
    public String toString(){
        return error.toString();
    }
}