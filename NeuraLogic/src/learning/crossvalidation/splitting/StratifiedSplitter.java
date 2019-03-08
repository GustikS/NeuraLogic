package learning.crossvalidation.splitting;

import learning.LearningSample;
import utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by gusta on 14.3.17.
 */
public class StratifiedSplitter<T extends LearningSample> implements Splitter<T> {
    private static final Logger LOG = Logger.getLogger(StratifiedSplitter.class.getName());

    @Override
    public List<Stream<T>> partition(Stream<T> samples, int foldCount) {
        return null;
    }

    @Override
    public List<List<T>> partition(List<T> samples, int foldCount) {

        //todo next;
        LOG.severe("StratifiedSplitter no implemented yet");
        return null;
    }

    @Override
    public Pair<List<T>, List<T>> partition(List<T> samples, double percentage) {
        //todo next
        List<T> validation = samples.subList(0, (int) percentage * samples.size());
        List<T> training = samples.subList((int) percentage * samples.size() + 1, samples.size()-1);
        return new Pair<>(training,validation);
    }

}
