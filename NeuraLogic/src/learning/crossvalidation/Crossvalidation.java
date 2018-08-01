package learning.crossvalidation;

import learning.Learner;
import constructs.example.LogicSample;
import networks.evaluation.results.Results;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class Crossvalidation {

    List<TrainTestResults> crossvalidate(){return null;}
    TrainTestResults aggregate(List<TrainTestResults> foldRunStatsList){return null;}
    Results train(Learner learner, List<LogicSample> learningSamples){return null;}
    Results test(Learner learner, List<LogicSample> learningSamples){return null;}

}
