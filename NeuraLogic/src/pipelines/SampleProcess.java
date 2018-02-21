package pipelines;

import learning.LearningSample;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Created by gusta on 17.3.17.
 */

@FunctionalInterface
public interface SampleProcess<T extends LearningSample> {
    Stream<T> process(Stream<T> input);

    default Stream<T> process(Collection<T> input) {
        return process(input.stream());
    }
}
