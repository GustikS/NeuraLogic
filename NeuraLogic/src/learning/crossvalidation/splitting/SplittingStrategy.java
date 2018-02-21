package learning.crossvalidation.splitting;

import learning.LearningSample;

import java.util.Collection;

/**
 * Created by gusta on 8.3.17.
 */
public interface SplittingStrategy {
    Collection<Collection<LearningSample>> splitIntoFolds(Collection<LearningSample> samples);
}
