package learning.crossvalidation;

import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Results;

/**
 * Created by gusta on 8.3.17.
 */
public class TrainTestResults {
    public Progress training;
    public Results testing;

    public TrainTestResults(Progress train, Results test) {
        this.training = train;
        this.testing = test;
    }
}
