package cz.cvut.fel.ida.learning.crossvalidation;

import cz.cvut.fel.ida.learning.results.Results;

import java.util.logging.Logger;

public class MeanStdResults {
    private static final Logger LOG = Logger.getLogger(MeanStdResults.class.getName());

    private final Results mean;
    private final Results std;

    public MeanStdResults(Results mean, Results std){
        this.mean = mean;
        this.std = std;
    }

    public static class TrainValTest{
        MeanStdResults train;
        MeanStdResults val;
        MeanStdResults test;

        public TrainValTest(MeanStdResults train, MeanStdResults val, MeanStdResults test){
            this.train = train;
            this.val = val;
            this.test = test;
        }

        //todo now tostring
    }
}
