package learning.crossvalidation;

import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Results;
import utils.exporting.Exportable;
import utils.exporting.Exporter;

/**
 * Created by gusta on 8.3.17.
 */
public class TrainTestResults implements Exportable<TrainTestResults> {
    public Progress training;
    public Results testing;

    public TrainTestResults(Progress train, Results test) {
        this.training = train;
        this.testing = test;
    }

    @Override
    public TrainTestResults export(Exporter exporter) {
        exporter.export(this);
        return this;
    }
}
