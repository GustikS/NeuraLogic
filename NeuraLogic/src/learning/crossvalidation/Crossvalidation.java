package learning.crossvalidation;

import com.sun.istack.internal.Nullable;
import learning.LearningSample;
import learning.crossvalidation.splitting.Splitter;
import networks.computation.results.Results;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by gusta on 8.3.17.
 * <p>
 * Crossval takes settings (and/or foldCount) to instantiate splitter.
 * It can then split incomming samples into folds.
 */
public class Crossvalidation<T extends LearningSample> {
    Settings settings;

    public int foldCount;
    Splitter<T> splitter;

    public List<Fold<T>> folds;

    List<T> samples;

    @Nullable
    Results results;

    public Crossvalidation(Settings settings) {
        this.settings = settings;
        foldCount = settings.foldsCount;
        splitter = Splitter.<T>getSplitter(settings);
    }

    public Crossvalidation(Settings settings, int foldCount) {
        folds = new ArrayList<>(foldCount);
        for (int i = 0; i < foldCount; i++) {
            folds.add(new Fold<>(i));
        }
        this.foldCount = foldCount;
        splitter = Splitter.<T>getSplitter(settings);
    }

    public void loadFolds(Stream<T> samples) {
        this.folds = splitter.splitIntoFolds(samples, foldCount);
        this.samples = extractSamples(this.folds);
    }

    public void loadFolds(List<Stream<T>> folds) {
        this.folds = splitter.splitIntoFolds(folds);
        this.samples = extractSamples(this.folds);
    }

    public TrainTestResults aggregateResults(List<TrainTestResults> foldRunStatsList) {
        return null;
    }

    private List<T> extractSamples(List<Fold<T>> folds) {
        List<T> samples = new ArrayList<>();
        folds.forEach(fold -> samples.addAll(fold.test));
        return samples;
    }
}