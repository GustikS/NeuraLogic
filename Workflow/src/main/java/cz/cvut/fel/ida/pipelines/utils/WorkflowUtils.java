package cz.cvut.fel.ida.pipelines.utils;

import cz.cvut.fel.ida.learning.results.ClassificationResults;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.logging.Logger;

public class WorkflowUtils {
    private static final Logger LOG = Logger.getLogger(WorkflowUtils.class.getName());

    public static Pair<Double, Duration> getDisperionAndTime(Pair<Pipeline, ?> results) {
        ClassificationResults classificationResults = (ClassificationResults) results.s;
        Double dispersion = classificationResults.dispersion;
        Duration timeTaken = results.r.timing.getTimeTaken();
        return new Pair<>(dispersion, timeTaken);
    }

    public static <T> Iterator<List<T>> consecutiveGroupsIterator(
            Iterator<T> source, Function<T, ?> classifier) {
        return new Iterator<>() {
            private List<T> currentGroup = null;
            private T nextElement = null;

            @Override
            public boolean hasNext() {
                return currentGroup != null || fetchNextGroup();
            }

            @Override
            public List<T> next() {
                if (!hasNext()) throw new NoSuchElementException();
                List<T> group = new ArrayList<>(currentGroup);
                currentGroup = null;
                return group;
            }

            private boolean fetchNextGroup() {
                currentGroup = new ArrayList<>();
                if (nextElement != null) {
                    currentGroup.add(nextElement);
                    nextElement = null;
                }

                while (source.hasNext()) {
                    T elem = source.next();
                    if (currentGroup.isEmpty() ||
                            classifier.apply(currentGroup.get(0)).equals(classifier.apply(elem))) {
                        currentGroup.add(elem);
                    } else {
                        nextElement = elem;  // Start next group
                        return true;
                    }
                }
                return !currentGroup.isEmpty();
            }
        };
    }
}
