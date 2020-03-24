package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class ClassificationResults extends RegressionResults {
    static Value oneHalf = new ScalarValue(0.5);       //CAREFUL FOR THE SWITCH OF SIDES WITH DD (must be declared as generic Value)

    private Double accuracy;
    private Double precision;
    private Double recall;
    private Double f_Measure;
    private Double majorityErr;
    private Double dispersion;

    private int goodCount;
    private int zeroCount;
    private int oneCount;

    public Value bestThreshold;
    public Double bestAccuracy;

    public ClassificationResults(List<Result> outputs, Settings settings) {
        super(outputs, settings);
    }

    @Override
    public boolean recalculate() {
        error = calculateErrorValue();

        loadBinaryMetrics(evaluations);

        if (settings.calculateBestThreshold) {
            getBestAccuracyThreshold(evaluations);
        }

        return true;   //todo rest of measures
    }

    public Value calculateErrorValue() {
        List<Value> errors = new ArrayList<>(evaluations.size());
        for (Result evaluation : evaluations) {
            errors.add(evaluation.errorValue());
        }
        return aggregationFcn.evaluate(errors);
    }

    /**
     * Calculates the standard metrics for binary classification
     *
     * @param evaluations
     */
    private void loadBinaryMetrics(List<Result> evaluations) {    //todo add multiclass evaluation?
        goodCount = 0;
        zeroCount = 0;
        oneCount = 0;
        Value zeroSum = new ScalarValue(0);
        Value oneSum = new ScalarValue(0);

        for (Result evaluation : evaluations) {
            if (evaluation.target.greaterThan(oneHalf)) {
                oneCount++;
                oneSum.incrementBy(evaluation.output);
                if (evaluation.output.greaterThan(oneHalf)) {
                    goodCount++;
                }
            } else {
                zeroCount++;
                zeroSum.incrementBy(evaluation.output);
                if (oneHalf.greaterThan(evaluation.output)) {
                    goodCount++;
                }
            }
        }
        majorityErr = Math.max(zeroCount, oneCount) / (double) evaluations.size();
        Value disp = oneSum.elementTimes(new ScalarValue(1.0 / oneCount)).minus(zeroSum.elementTimes(new ScalarValue(1.0 / zeroCount)));    // = average positive output minus average negative output
        dispersion = ((ScalarValue) disp).value;
        accuracy = (double) goodCount / evaluations.size();
    }

    public Double getBestAccuracy(List<Result> evaluations, Value trainedThreshold) {
        int TP = 0;
        int TN = 0;
        for (Result evaluation : evaluations) {
            if (evaluation.output.greaterThan(trainedThreshold) && evaluation.target.greaterThan(trainedThreshold)) {
                TP++;
            } else if (trainedThreshold.greaterThan(evaluation.output) && trainedThreshold.greaterThan(evaluation.target)) {
                TN++;
            }
        }
        bestThreshold = trainedThreshold;
        bestAccuracy = ((double) (TP + TN)) / evaluations.size();
        return bestAccuracy;
    }

    public void getBestAccuracyThreshold(List<Result> evaluations) {
        Collections.sort(evaluations);

        double allCount = evaluations.size();

        int cumNegCount = 0;
        int cumPosCount = 0;

        double bestCumErr = evaluations.size();
        int bestIndex = -1;

        int i = 0;
        while (true) {
            if (i >= evaluations.size()) {
                break;
            }
            double cumErr = (cumPosCount + zeroCount - cumNegCount) / allCount;
            if (cumErr < bestCumErr) {
                bestIndex = i;
                bestCumErr = cumErr;
            }
            do {
                Result evaluation = evaluations.get(i);
                if (evaluation.target.greaterThan(oneHalf)) {
                    cumPosCount++;
                } else {
                    cumNegCount++;
                }
                i++;
            } while (i < evaluations.size() && evaluations.get(i).output.equals(evaluations.get(i - 1).output));
        }

        try {
            bestThreshold = evaluations.get(bestIndex).output;
        } catch (IndexOutOfBoundsException e) {
            bestThreshold = evaluations.get(0).output;
        }
        if (bestIndex - 1 >= 0) {
            bestThreshold = (bestThreshold.plus(evaluations.get(bestIndex - 1).output)).times(oneHalf);
        }
        bestAccuracy = 1 - bestCumErr;
    }

    @Override
    public String toString() {
        return this.toString(null);
    }

    @Override
    public String toString(Settings settings) {
        StringBuilder sb = new StringBuilder();
        if (accuracy != null)
            sb.append("accuracy: " + Settings.shortNumberFormat.format(accuracy.doubleValue() * 100) + "% (maj. " + Settings.shortNumberFormat.format(majorityErr * 100) + "%)");
        if (bestAccuracy != null) {
            sb.append("(best thresh acc: " + Settings.shortNumberFormat.format(bestAccuracy * 100) + "%)");
        }
        if (dispersion != null) {
            sb.append(", disp: " + dispersion.toString());
        }
        if (error != null) {
            if (settings == null) {
                sb.append(", error: ").append(error.toString());
            } else {
                String errAggfcn = settings.errorAggregationFcn.toString();
                String errFcn = settings.errorFunction.toString();
                String errString = errAggfcn + "(" + errFcn + ")";
                sb.append(", error: ").append(errString).append(" = ").append(error.toString());
            }
        }
        return sb.toString();
    }
}