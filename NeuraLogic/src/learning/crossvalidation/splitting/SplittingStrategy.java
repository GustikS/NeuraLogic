package learning.crossvalidation.splitting;

import settings.Settings;

import java.util.Collection;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class SplittingStrategy<T> {
    abstract Collection<Collection<T>> splitIntoFolds(Collection<T> samples);

    public SplittingStrategy getSplitter(Settings settings){
        if (settings.stratification) {
            return new StratifiedSplitter<T>();
        }
        //TODO
        return new StratifiedSplitter();
    }
}
