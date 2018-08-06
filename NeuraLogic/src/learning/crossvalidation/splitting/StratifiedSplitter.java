package learning.crossvalidation.splitting;

import learning.LearningSample;
import learning.crossvalidation.Fold;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by gusta on 14.3.17.
 */
public class StratifiedSplitter<T extends LearningSample> extends Splitter<T> {

    @Override
    List<Fold<T>> splitIntoFolds(Stream<T> samples) {
        return null;
    }

}
