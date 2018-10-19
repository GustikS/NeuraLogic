package learning.crossvalidation;

import networks.computation.results.Results;

/**
 * Created by gusta on 8.3.17.
 */
public class TrainTestResults {
    public Results training;
    public Results testing;

    public TrainTestResults(Results train, Results test) {
        this.training = train;
        this.testing = test;
    }
}
