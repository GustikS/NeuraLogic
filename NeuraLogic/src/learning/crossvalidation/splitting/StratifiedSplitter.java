package learning.crossvalidation.splitting;

import learning.LearningSample;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by gusta on 14.3.17.
 */
public class StratifiedSplitter<T extends LearningSample> implements Splitter<T> {


    @Override
    public List<Stream<T>> partition(Stream<T> samples, int foldCount) {
        return null;
    }

    @Override
    public List<List<T>> partition(List<T> samples, int foldCount) {
        return null;
    }

}
