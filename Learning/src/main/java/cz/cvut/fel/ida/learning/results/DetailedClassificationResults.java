package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.results.metrics.AUC;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class DetailedClassificationResults extends ClassificationResults {  //todo now put this automatically at the end of learning
    private static final Logger LOG = Logger.getLogger(ClassificationResults.class.getName());

    public Value bestThreshold = new ScalarValue(0.5);
    public Double bestAccuracy;

    private Double precision;
    private Double recall;
    private Double f_Measure;

    private Double AUCroc;
    private Double AUCrocJesse;
    private Double AUCpr;

    public DetailedClassificationResults(List<Result> outputs, Settings aggregationFcn) {
        super(outputs, aggregationFcn);
    }

    @Override
    public boolean recalculate() {
        super.recalculate();
        if (settings.forceDetailedResults){
            computeDetailedMetrics(evaluations);
        }
        return true;
    }

    public void computeDetailedMetrics(List<Result> evaluations) {
        AUCroc = calculateAUC(evaluations);

        if (settings.alternativeAUC)
            AUCrocJesse = getAlternativeAUC(evaluations);

        AUCpr = getAlternativeAUCPR(evaluations);

        computeBestAccuracyThreshold(evaluations);
        computeDetailedMetrics(evaluations, bestThreshold);
    }

    public Double computeDetailedMetrics(List<Result> evaluations, Value trainedThreshold) {
        int TP = 0;
        int TN = 0;
        for (Result evaluation : evaluations) {
            if (evaluation.getOutput().greaterThan(trainedThreshold) && evaluation.getTarget().greaterThan(trainedThreshold)) {
                TP++;
            } else if (trainedThreshold.greaterThan(evaluation.getOutput()) && trainedThreshold.greaterThan(evaluation.getTarget())) {
                TN++;
            }
        }
        bestThreshold = trainedThreshold;
        bestAccuracy = ((double) (TP + TN)) / evaluations.size();
        return bestAccuracy;
    }

    public void computeBestAccuracyThreshold(List<Result> evaluations) {
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
                if (evaluation.getTarget().greaterThan(oneHalf)) {
                    cumPosCount++;
                } else {
                    cumNegCount++;
                }
                i++;
            } while (i < evaluations.size() && evaluations.get(i).getOutput().equals(evaluations.get(i - 1).getOutput()));
        }

        try {
            bestThreshold = evaluations.get(bestIndex).getOutput();
        } catch (IndexOutOfBoundsException e) {
            bestThreshold = evaluations.get(0).getOutput();
        }
        if (bestIndex - 1 >= 0) {
            bestThreshold = (bestThreshold.plus(evaluations.get(bestIndex - 1).getOutput())).times(oneHalf);
        }
        bestAccuracy = 1 - bestCumErr;
    }

    public double calculateAUC(List<Result> evaluations) {

        double pos = oneCount;
        double neg = zeroCount;

        Collections.sort(evaluations);

        double[] ranks = new double[evaluations.size()];
        for (int i = 0; i < evaluations.size(); i++) {
            if (i == evaluations.size() - 1 || !evaluations.get(i).getOutput().equals(evaluations.get(i + 1).getOutput())) {
                ranks[i] = i + 1;
            } else {
                int j = i + 1;
                for (; j < evaluations.size() && evaluations.get(j).getOutput() == evaluations.get(i).getOutput(); j++)
                    ;

                double r = (i + 1 + j) / 2.0;
                for (int k = i; k < j; k++) {
                    ranks[k] = r;
                }
                i = j - 1;
            }
        }

        double auc = 0.0;
        for (int i = 0; i < evaluations.size(); i++) {
            if (evaluations.get(i).getTarget().greaterThan(oneHalf))
                auc += ranks[i];
        }

        auc = (auc - (pos * (pos + 1) / 2.0)) / (pos * neg);
        return auc;
    }

    /**
     * This one from Jesse seems to be somewhat higher (better interpolation maybe)
     *
     * @param evaluations
     * @return
     */
    public double getAlternativeAUC(List<Result> evaluations) {
        return AUC.getAUCroc(evaluations);
//        return 0;
    }

    /**
     * This one from Jesse seems to be somewhat higher (better interpolation maybe)
     *
     * @param evaluations
     * @return
     */
    public double getAlternativeAUCPR(List<Result> evaluations) {
        return AUC.getAUCpr(evaluations);
//        return 0;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String toString(Settings settings) {
        String s = super.toString(settings);
        StringBuilder sb = new StringBuilder(s);
        if (bestAccuracy != null) {
            sb.append(", (best thresh acc: " + Settings.shortNumberFormat.format(bestAccuracy * 100) + "%)");
        }
        sb.append(" (maj. " + Settings.shortNumberFormat.format(majorityErr * 100) + "%)");
        if (AUCroc != null) {
            sb.append(", (AUC-ROC: " + Settings.detailedNumberFormat.format(AUCroc) + ")");
        }
        if (AUCrocJesse != null) {
            sb.append(", (AUC-ROC [Jesse]: " + Settings.detailedNumberFormat.format(AUCrocJesse) + ")");
        }
        if (AUCpr != null) {
            sb.append(", (AUC-PR [Jesse]: " + Settings.detailedNumberFormat.format(AUCpr) + ")");
        }
        return sb.toString();
    }
}
