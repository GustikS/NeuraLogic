package networks.computation.evaluation.results;

import networks.computation.evaluation.functions.Aggregation;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class DetailedClassificationResults extends ClassificationResults {
    private Double AUCpr;
    private Double AUCroc;

    public DetailedClassificationResults(List<Result> outputs, Aggregation aggregationFcn) {
        super(outputs, aggregationFcn);
    }

    @Override
    public boolean recalculate() {
        super.recalculate();
        calculateAUC();
        return true;
    }

    private void calculateAUC() {
        //todo
    }
}
