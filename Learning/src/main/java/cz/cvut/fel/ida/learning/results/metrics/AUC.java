package cz.cvut.fel.ida.learning.results.metrics;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.learning.results.metrics.Jesse.Confusions;
import cz.cvut.fel.ida.learning.results.metrics.Jesse.ROCpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class AUC {
    private static final Logger LOG = Logger.getLogger(AUC.class.getName());

    private static Value oneHalf = new ScalarValue(0.5);

    Confusions confusion;

    public AUC(List<Result> evaluations){
         this.confusion = getConfusion(evaluations);
    }

    public double getAUCroc() {
        double aucroc = confusion.calculateAUCROC();
        return aucroc;
    }

    public static double getAUCroc(List<Result> evaluations) {
        double aucroc = getConfusion(evaluations).calculateAUCROC();
        return aucroc;
    }

    public double getAUCpr() {
        try {
            double aucpr = confusion.calculateAUCPR(0.0);
            return aucpr;
        } catch (Exception e){
            LOG.severe("Could not calculate AUC PR!");
            return  0;
        }
    }

    public static double getAUCpr(List<Result> evaluations) {
        double aucpr = getConfusion(evaluations).calculateAUCPR(0.0);
        return aucpr;
    }

    public static Confusions getConfusion(List<Result> resultList) {

        int positiveClass = 0;
        int negativeClass = 0;

        Result[] results = new Result[resultList.size()];
        Arrays.sort(resultList.toArray(results));

        ArrayList<ROCpoint> roCpoints = new ArrayList<>();

        Value lastOutput = results[results.length - 1].getOutput();

        if (results[results.length - 1].getTarget().greaterThan(oneHalf)) {
            ++positiveClass;
        } else {
            ++negativeClass;
        }

        for (int tmpInt = results.length - 2; tmpInt >= 0; --tmpInt) {
            Value output = results[tmpInt].getOutput();
            Value target = results[tmpInt].getTarget();
            if (!output.equals(lastOutput)) {
                roCpoints.add(new ROCpoint(positiveClass, negativeClass));
            }

            lastOutput = output;
            if (target.greaterThan(oneHalf)) {
                ++positiveClass;
            } else {
                ++negativeClass;
            }
        }

        roCpoints.add(new ROCpoint(positiveClass, negativeClass));
        Confusions confusions = new Confusions(positiveClass, negativeClass);

        for (ROCpoint roCpoint : roCpoints) {
            confusions.addPoint(roCpoint.posPosition, roCpoint.negPosition);
        }

        confusions.sort();
        confusions.interpolate();

        return confusions;
    }


}
