package learning.crossvalidation;

import exporting.Exportable;
import exporting.Exporter;
import learning.results.Progress;
import learning.results.Results;

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
