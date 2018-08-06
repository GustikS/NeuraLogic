package learning.crossvalidation.splitting;

import learning.LearningSample;
import learning.crossvalidation.Fold;
import settings.Settings;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Splitter<T extends LearningSample> {

    /**
     * Straightforward load everything and split
     * @param samples
     * @return
     */
    abstract List<Fold<T>> splitIntoFolds(Stream<T> samples);

    /**
     * Optional online splitting implementation with higher complexity (heap)
     * @param samples
     * @return
     */
    @Deprecated
    Stream<Stream<T>> splitIntoStreams(Stream<T> samples){
        //TODO?
        return null;
    }

    public Splitter getSplitter(Settings settings){
        if (settings.stratification) {
            return new StratifiedSplitter<T>();
        }
        //TODO
        return new StratifiedSplitter();
    }
}
