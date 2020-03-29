package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.algebra.utils.MathUtils;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.crossvalidation.MeanStdResults;
import cz.cvut.fel.ida.setup.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gusta on 8.3.17.
 */
public class ClassificationResults extends RegressionResults {
    private static final Logger LOG = Logger.getLogger(ClassificationResults.class.getName());

    static Value oneHalf = new ScalarValue(0.5);       //CAREFUL FOR THE SWITCH OF SIDES WITH DD (must be declared as generic Value)

    private Double accuracy;
    public Double majorityErr;
    public Double dispersion;

    private int goodCount;
    protected int zeroCount;
    protected int oneCount;

    public ClassificationResults(List<Result> outputs, Settings settings) {
        super(outputs, settings);
    }

    protected ClassificationResults(Value error, Double accuracy, Double majorityErr, Double dispersion) {
        super(error);
        this.accuracy = accuracy;
        this.majorityErr = majorityErr;
        this.dispersion = dispersion;
    }

    @Override
    public boolean recalculate() {
        error = calculateErrorValue();

        loadBasicCounts(evaluations);
        loadBinaryMetrics(evaluations);

//        if (settings.calculateBestThreshold) {
//            getBestAccuracyThreshold(evaluations);
//        }

        return true;   //todo rest of measures
    }

    public Value calculateErrorValue() {
        List<Value> errors = new ArrayList<>(evaluations.size());
        for (Result evaluation : evaluations) {
            errors.add(evaluation.errorValue());
        }
        return aggregationFcn.evaluate(errors);
    }

    public static MeanStdResults aggregateClassifications(List<ClassificationResults> resultsList) {
        List<Value> errors = resultsList.stream().map(res -> res.error).collect(Collectors.toList());
        Value meanError = MathUtils.getMeanValue(errors);
        Value stdError = MathUtils.getStd(errors, meanError);

        List<Double> accuracies = resultsList.stream().map(res -> res.accuracy).collect(Collectors.toList());
        Double meanAcc = MathUtils.getMean(accuracies);
        Double stdAcc = MathUtils.getStd(accuracies, meanAcc);

        List<Double> dispersions = resultsList.stream().map(res -> res.dispersion).collect(Collectors.toList());
        Double meanDisp = MathUtils.getMean(dispersions);
        Double stdDisp = MathUtils.getStd(dispersions, meanAcc);

        List<Double> majorErrs = resultsList.stream().map(res -> res.majorityErr).collect(Collectors.toList());
        Double meanMajErr = MathUtils.getMean(majorErrs);
        Double stdMajErr = MathUtils.getStd(majorErrs, meanMajErr);

        ClassificationResults mean = new ClassificationResults(meanError, meanAcc, meanMajErr, meanDisp);
        ClassificationResults std = new ClassificationResults(stdError, stdAcc, stdMajErr, stdDisp);
        return new MeanStdResults(mean, std);
    }

    private void loadBasicCounts(List<Result> evaluations){
        zeroCount = 0;
        oneCount = 0;

        for (Result evaluation : evaluations) {
            if (evaluation.getTarget().greaterThan(oneHalf)) {
                oneCount++;
            } else {
                zeroCount++;
            }
        }

        majorityErr = Math.max(zeroCount, oneCount) / (double) evaluations.size();
    }

    /**
     * Calculates the standard metrics for binary classification
     *
     * @param evaluations
     */
    private void loadBinaryMetrics(List<Result> evaluations) {    //todo add multiclass evaluation?
        goodCount = 0;

        Value zeroSum = new ScalarValue(0);
        Value oneSum = new ScalarValue(0);

        for (Result evaluation : evaluations) {
            if (evaluation.getTarget().greaterThan(oneHalf)) {
//                oneCount++;
                oneSum.incrementBy(evaluation.getOutput());
                if (evaluation.getOutput().greaterThan(oneHalf)) {
                    goodCount++;
                }
            } else {
//                zeroCount++;
                zeroSum.incrementBy(evaluation.getOutput());
                if (oneHalf.greaterThan(evaluation.getOutput())) {
                    goodCount++;
                }
            }
        }
        Value disp = oneSum.elementTimes(new ScalarValue(1.0 / oneCount)).minus(zeroSum.elementTimes(new ScalarValue(1.0 / zeroCount)));    // = average positive output minus average negative output
        dispersion = ((ScalarValue) disp).value;
        accuracy = (double) goodCount / evaluations.size();
    }


    @Override
    public String toString() {
        return this.toString(null);
    }

    @Override
    public String toString(Settings settings) {
        StringBuilder sb = new StringBuilder();
        if (accuracy != null)
            sb.append("accuracy: " + Settings.shortNumberFormat.format(accuracy.doubleValue() * 100));
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