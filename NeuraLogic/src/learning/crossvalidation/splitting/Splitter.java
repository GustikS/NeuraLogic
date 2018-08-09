package learning.crossvalidation.splitting;

import learning.LearningSample;
import learning.crossvalidation.Fold;
import org.jetbrains.annotations.NotNull;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gusta on 8.3.17.
 */
public interface Splitter<T extends LearningSample> {

    /**
     * Straightforward load everything and split
     *
     * @param samples
     * @return
     */
    List<Stream<T>> partition(Stream<T> samples, int foldCount);

    default List<Fold<T>> splitIntoFolds(Stream<T> samples, int foldCount) {
        return splitIntoFolds(partition(samples, foldCount));
    }

    default List<Fold<T>> splitIntoFolds(List<Stream<T>> streams) {
        List<List<T>> folds = new ArrayList<>(streams.size());
        for (Stream<T> fold : streams) {
            folds.add(fold.collect(Collectors.toList()));
        }

        List<Fold<T>> cvf = new ArrayList<>(streams.size());
        for (int i = 0; i < folds.size(); i++) {
            Fold<T> fold = new Fold<>(i);
            fold.test = folds.get(i);
            fold.train = new ArrayList<>();
            for (int j = 0; j < folds.size(); j++) {
                if (j == i) continue;
                ;
                fold.train.addAll(folds.get(j));
            }
            cvf.add(fold);
        }
        return cvf;
    }

    /**
     * Optional online splitting implementation with higher complexity (heap)
     *
     * @param samples
     * @return
     */
    @Deprecated
    default Stream<Stream<T>> splitIntoStreams(Stream<T> samples) {
        return null;
    }

    @NotNull
    static <T extends LearningSample> Splitter<T> getSplitter(Settings settings) {
        if (settings.stratification) {
            return new StratifiedSplitter<>();
        }
        //TODO
        return new StratifiedSplitter<>();
    }
}