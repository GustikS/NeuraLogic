package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.utils.MathUtils;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.learning.crossvalidation.MeanStdResults;
import cz.cvut.fel.ida.setup.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gusta on 8.3.17.
 */
public class ClassificationResults extends RegressionResults {
    private static final Logger LOG = Logger.getLogger(ClassificationResults.class.getName());

    static Value oneHalf = new ScalarValue(0.5);       //CAREFUL FOR THE SWITCH OF SIDES WITH DD (must be declared as generic Value)

    public Double accuracy;
    public Double majorityAcc;
    public Double dispersion;

    private int goodCount;
    protected int zeroCount;
    protected int oneCount;

    public ClassificationResults(List<Result> outputs, Settings settings) {
        super(outputs, settings);
    }

    protected ClassificationResults(Value error, Double accuracy, Double majorityAcc, Double dispersion) {
        super(error);
        this.accuracy = accuracy;
        this.majorityAcc = majorityAcc;
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
        if (errors.isEmpty() || errors.get(0) == null) {
            return null;
        }

        Value meanError = MathUtils.getMeanValue(errors);
        Value stdError = MathUtils.getStd(errors, meanError);

        List<Double> accuracies = resultsList.stream().map(res -> res.accuracy).collect(Collectors.toList());
        Double meanAcc = MathUtils.getMean(accuracies);
        Double stdAcc = MathUtils.getStd(accuracies, meanAcc);

        List<Double> dispersions = resultsList.stream().map(res -> res.dispersion).collect(Collectors.toList());
        Double meanDisp = MathUtils.getMean(dispersions);
        Double stdDisp = MathUtils.getStd(dispersions, meanDisp);

        List<Double> majorErrs = resultsList.stream().map(res -> res.majorityAcc).collect(Collectors.toList());
        Double meanMajErr = MathUtils.getMean(majorErrs);
        Double stdMajErr = MathUtils.getStd(majorErrs, meanMajErr);

        ClassificationResults mean = new ClassificationResults(meanError, meanAcc, meanMajErr, meanDisp);
        ClassificationResults std = new ClassificationResults(stdError, stdAcc, stdMajErr, stdDisp);
        return new MeanStdResults(mean, std);
    }

    private void loadBasicCounts(List<Result> evaluations) {
        if (!(evaluations.get(0).getTarget() instanceof ScalarValue)) {
            return;
        }

        zeroCount = 0;
        oneCount = 0;

        for (Result evaluation : evaluations) {
            if (evaluation.getTarget().greaterThan(oneHalf)) {
                oneCount++;
            } else {
                zeroCount++;
            }
        }

        majorityAcc = Math.max(zeroCount, oneCount) / (double) evaluations.size();
    }

    /**
     * Calculates the standard metrics for binary classification
     *
     * @param evaluations
     */
    private void loadBinaryMetrics(List<Result> evaluations) {

        if ((evaluations.get(0).getTarget() instanceof VectorValue)) {
            loadMulticlassMetrics(evaluations);
            return;
        }

        if (settings.squishLastLayer){  //this means that the outputs are not normalized!
            for (Result evaluation : evaluations) {
                evaluation.setOutput(ElementWise.Singletons.sigmoid.evaluate(evaluation.getOutput()));
            }
        }

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

    private void loadMulticlassMetrics(List<Result> evaluations) {
        goodCount = 0;

        HashMap<VectorValue, VectorValue> classAcums = new HashMap<>();
        HashMap<VectorValue, Integer> classCounts = new HashMap<>();

        if (settings.squishLastLayer){  //this means that the outputs are not normalized between 0-1!
            for (Result evaluation : evaluations) {
                evaluation.setOutput(Transformation.Singletons.softmax.evaluate(evaluation.getOutput()));
            }
        }

        for (Result evaluation : evaluations) {
            VectorValue value = null;
            try {
                value = (VectorValue) evaluation.getTarget();
            } catch (ClassCastException e) {
                LOG.severe("Unsupported target class dimensionality (only scalars or vectors are assumed)");
                return;
            }

            Value classAcum = classAcums.get(value);
            if (classAcum == null) {
                classAcums.put((VectorValue) evaluation.getTarget(), (VectorValue) evaluation.getOutput().clone());
                classCounts.put((VectorValue) evaluation.getTarget(), 1);
            } else {
                classAcum.incrementBy(evaluation.getOutput());
                classCounts.put((VectorValue) evaluation.getTarget(), classCounts.get(evaluation.getTarget()) + 1);
            }
            int maxInd = evaluation.getOutput().getMaxInd();
            if (evaluation.getTarget().getMaxInd() == maxInd) {
                if (((VectorValue) evaluation.getOutput()).values[maxInd] != 0.0) { //this (default) output doesn't count as a prediction
                    goodCount++;
                } else {
//                    System.out.println();
                }
            } else {
//                System.out.println();
            }
        }

        dispersion = 0.0;
        for (Map.Entry<VectorValue, VectorValue> entry : classAcums.entrySet()) {   //for each class
            int maxInd = entry.getKey().getMaxInd();

            VectorValue value = (VectorValue) entry.getValue();
            double norm = value.values[maxInd] / classCounts.get(entry.getKey());   //normalize the accumulated predicted value for the correct target class
            dispersion += norm;
        }
        dispersion /= classCounts.size();   //normalize over the number of classes (so that max disp. == 1)

        int maxCount = classCounts.values().stream().mapToInt(Integer::intValue).max().getAsInt();

        majorityAcc = (double) maxCount / evaluations.size();
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
            sb.append("accuracy: " + Settings.shortNumberFormat.format(accuracy.doubleValue() * 100) + "%");
        if (dispersion != null) {
            sb.append(", disp: " + dispersion.toString());
        }
        if (error != null) {
            if (settings == null) {
                sb.append(", error: ").append(error.toDetailedString());
            } else {
                String errAggfcn = settings.errorAggregationFcn.toString();
                String errFcn = settings.errorFunction.toString();
                String errString = errAggfcn + "(" + errFcn + ")";
                sb.append(", error: ").append(errString).append(" = ").append(error.toDetailedString());
            }
        }
        return sb.toString();
    }
}