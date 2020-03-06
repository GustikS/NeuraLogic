package learning.crossvalidation.splitting;

import learning.LearningSample;
import settings.Settings;
import utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class LinearSplitter<T extends LearningSample> implements Splitter<T> {
    private static final Logger LOG = Logger.getLogger(LinearSplitter.class.getName());
    private Settings settings;

    public LinearSplitter(Settings settings) {
        this.settings = settings;
    }

    @Override
    public List<Stream<T>> partition(Stream<T> samples, int foldCount) {
        return null;
    }

    @Override
    public List<List<T>> partition(List<T> samples, int foldCount) {
        return null;
    }

    @Override
    public Pair<List<T>, List<T>> partition(List<T> samples, double percentage) {
        int split = (int) percentage * samples.size();
        if (percentage!= 1.0 && split == 1 || split == samples.size()){
            LOG.severe("Problem with samples partitioning, there are too few to be splitted: " + split + " out of " + samples.size());
        }
        List<T> training = samples.subList(0, split);
        List<T> validation = samples.subList(split, samples.size());
        return new Pair<>(training, validation);
    }
}
