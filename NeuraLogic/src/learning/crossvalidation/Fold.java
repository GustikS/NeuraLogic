package learning.crossvalidation;

import learning.LearningSample;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class Fold<T extends LearningSample> {
    private static final Logger LOG = Logger.getLogger(Fold.class.getName());

    int id;
    List<T> train;
    List<T> test;

    Optional<TrainTestResults> results;
}