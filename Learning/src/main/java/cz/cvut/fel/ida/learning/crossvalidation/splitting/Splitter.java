package cz.cvut.fel.ida.learning.crossvalidation.splitting;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.learning.LearningSample;
import cz.cvut.fel.ida.learning.crossvalidation.Fold;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.setup.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static cz.cvut.fel.ida.utils.generic.Utilities.terminateSampleStream;

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

    List<List<T>> partition(List<T> samples, int foldCount);

    Pair<List<T>, List<T>> partition(List<T> samples, double percentage);

    default List<Fold<T>> splitIntoFolds(Stream<T> samples, int foldCount) {
        return splitIntoFolds(partition(samples, foldCount));
    }


    default List<Fold<T>> splitIntoFolds(List<Stream<T>> streams) {
        List<List<T>> folds = new ArrayList<>(streams.size());
        for (Stream<T> fold : streams) {
            folds.add(terminateSampleStream(fold));
        }

        List<Fold<T>> cvf = new ArrayList<>(streams.size());
        for (int i = 0; i < folds.size(); i++) {
            Fold<T> fold = new Fold<>(i);
            fold.test = folds.get(i);
            fold.train = new ArrayList<>();
            for (int j = 0; j < folds.size(); j++) {
                if (j == i) continue;
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
            return new StratifiedSplitter<>(settings);
        } else {
            return new LinearSplitter<>(settings);
        }
    }
}