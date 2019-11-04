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
    private Double f_Measure;
    private Double majorityErr;
    private Double dispersion;

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

        return false;   //todo rest
    }

    private Double getAccuracy(List<Result> evaluations) {
        Value oneHalf = new ScalarValue(0.5);       //CAREFUL FOR THE SWITCH OF SIDES WITH DD (must be declared as generic Value)
        int goodCount = 0;
        int zeroCount = 0;
        int oneCount = 0;
        for (Result evaluation : evaluations) {
            if (evaluation.target.greaterThan(oneHalf)) {
                oneCount++;
                if (evaluation.output.greaterThan(oneHalf)) {
                    goodCount++;
                }
            }
            if (oneHalf.greaterThan(evaluation.target)) {
                zeroCount++;
                if (oneHalf.greaterThan(evaluation.output)) {
                    goodCount++;
                }
            }
        }
        majorityErr = Math.max(zeroCount, oneCount) / (double) evaluations.size();
        return (double) goodCount / evaluations.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (accuracy != null)
            sb.append("accuracy: " + Settings.numberFormat.format(accuracy.doubleValue() * 100) + "% (majority " + majorityErr * 100 + "%)");
        if (error != null)
            sb.append(", error function value: " + error.toString());
        return sb.toString();
    }
}