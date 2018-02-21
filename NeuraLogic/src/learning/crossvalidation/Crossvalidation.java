package learning.crossvalidation;

import learning.Learner;
import learning.LearningSample;
import training.results.Results;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class Crossvalidation {

    List<FoldRunStats> crossvalidate(){return null;}
    FoldRunStats aggregate(List<FoldRunStats> foldRunStatsList){return null;}
    Results train(Learner learner, List<LearningSample> learningSamples){return null;}
    Results test(Learner learner, List<LearningSample> learningSamples){return null;}

}
