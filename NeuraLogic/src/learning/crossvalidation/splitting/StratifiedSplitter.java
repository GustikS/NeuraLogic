package learning.crossvalidation.splitting;

import learning.LearningSample;
import networks.computation.evaluation.values.Value;
import utils.generic.Pair;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gusta on 14.3.17.
 */
public class StratifiedSplitter<T extends LearningSample> implements Splitter<T> {
    private static final Logger LOG = Logger.getLogger(StratifiedSplitter.class.getName());

    @Override
    public List<Stream<T>> partition(Stream<T> samples, int foldCount) {
        List<List<T>> partition = partition(samples.collect(Collectors.toList()), foldCount);
        return partition.stream().map(l -> l.stream()).collect(Collectors.toList());
    }

    @Override
    public List<List<T>> partition(List<T> samples, int foldCount) {
        List<List<T>> folds = new ArrayList<>();
        for (int i = 0; i < foldCount; i++) {
            folds.add(new ArrayList<>());
        }
        Map<Value, List<T>> classes = getClasses(samples);
        distributeUniformly(classes.values(), folds);
        return folds;
    }

    public List<T> getStratifiedSubset(List<T> samples, int appCount) {
        if (appCount > samples.size()) {
            LOG.warning("Limiting samples to a greater number than there actually is!");
            appCount = samples.size();
        }
        List<T> subset = representativeSubset(getClasses(samples).values(), (double) appCount / samples.size());
        if (subset.size() > appCount)
            subset = subset.subList(0, appCount);
        if (subset.size() == 0) {
            subset.add(samples.get(0));
        }
        return subset;
    }

    /**
     * Uniformly distribute collection of samples for each class into a list of folds.
     * Each fold will have app. the same distribution as the whole set.
     *
     * @param classes
     * @param folds
     */
    public void distributeUniformly(Collection<List<T>> classes, List<List<T>> folds) {
        for (List<T> classSamples : classes) {
            for (int i = 0; i < classSamples.size(); i++) {
                folds.get(i % folds.size()).add(classSamples.get(i));
            }
        }
    }

    /**
     * Get a subset with the same distribution of classes.
     *
     * @param classes
     * @param percentage
     */
    public List<T> representativeSubset(Collection<List<T>> classes, double percentage) {
        List<List<T>> subsets = classes.stream().map(l -> l.subList(0, (int) (l.size() * percentage))).collect(Collectors.toList());
        LOG.info("Calculated stratified subset with distribution: " + subsets.stream().map(List::size).collect(Collectors.toList()));
        List<T> collect = subsets.stream().flatMap(List::stream).collect(Collectors.toList());
        return collect;
    }

    @Override
    public Pair<List<T>, List<T>> partition(List<T> samples, double percentage) {
        int split = (int) percentage * samples.size();
        if (percentage != 1.0 && split == 1 || split == samples.size()) {
            LOG.warning("Problem with samples partitioning, there are too few to be splitted: " + split + " out of " + samples.size() + " (split percentage = " + percentage + ")");
        }
        List<T> train = representativeSubset(getClasses(samples).values(), percentage);
        List<T> test = new ArrayList<>(samples);
        boolean b = test.removeAll(train);
        return new Pair<>(train, test);
    }

    public Map<Value, List<T>> getClasses(List<T> samples) {
        Map<Value, List<T>> classes = new LinkedHashMap<>();
        for (T sample : samples) {
            List<T> tList = classes.computeIfAbsent(sample.target, k -> new ArrayList<>());
            tList.add(sample);
        }
        return classes;
    }

}
