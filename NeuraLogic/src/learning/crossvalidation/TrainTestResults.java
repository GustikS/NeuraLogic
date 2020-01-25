package learning.crossvalidation;

import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Results;
import utils.exporting.Exportable;
import utils.exporting.Exporter;

/**
 * Created by gusta on 8.3.17.
 */
public class TrainTestResults implements Exportable {
    public Progress training;
    public Results testing;

    public TrainTestResults(Progress train, Results test) {
        this.training = train;
        this.testing = test;
    }

    @Override
    public void export(Exporter exporter) {
        Progress progress = new Progress();
        progress.bestResults = training.bestResults;
        TrainTestResults trainTestResults = new TrainTestResults(progress, testing);
        exporter.export(trainTestResults);
    }
}
