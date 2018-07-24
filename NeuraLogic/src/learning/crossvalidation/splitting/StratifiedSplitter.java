package learning.crossvalidation.splitting;

import java.util.Collection;

/**
 * Created by gusta on 14.3.17.
 */
public class StratifiedSplitter<T> extends SplittingStrategy<T> {

    @Override
    Collection<Collection<T>> splitIntoFolds(Collection<T> samples) {
        return null;
    }
}
