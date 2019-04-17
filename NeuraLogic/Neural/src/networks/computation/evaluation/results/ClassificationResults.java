package networks.computation.evaluation.results;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class ClassificationResults extends RegressionResults {
    private Double accuracy;
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
        accuracy = getAccuracy(evaluations);

        return false;   //todo next rest
    }

    private Double getAccuracy(List<Result> evaluations) {
        Value oneHalf = new ScalarValue(0.5);       //CAREFUL FOR THE SWITCH OF SIDES WITH DD (must be declared as generic Value)
        int goodCount = 0;
        for (Result evaluation : evaluations) {
            if (evaluation.output.greaterThan(oneHalf) && evaluation.target.greaterThan(oneHalf)
                    || oneHalf.greaterThan(evaluation.output) && oneHalf.greaterThan(evaluation.target)) {
                goodCount++;
            }
        }
        return (double) goodCount / evaluations.size();
    }

    @Override
    public String toString() {
        return "accuracy: " + Settings.nf.format(accuracy.doubleValue()*100) + "%, error function value: " + error.toString();
    }
}