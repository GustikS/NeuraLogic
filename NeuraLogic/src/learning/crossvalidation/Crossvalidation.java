package learning.crossvalidation;

import learning.LearningSample;
import networks.evaluation.results.Results;

import java.util.List;
import java.util.Optional;

/**
 * Created by gusta on 8.3.17.
 */
public class Crossvalidation<T extends LearningSample> {

    List<Fold<T>> folds;
    Optional<Results> results;

    public TrainTestResults aggregateResults(List<TrainTestResults> foldRunStatsList){return null;}

}
