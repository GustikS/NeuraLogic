package pipeline;

import constructs.example.LogicSample;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Created by gusta on 17.3.17.
 */

@FunctionalInterface
@Deprecated
public interface SampleProcess<T extends LogicSample> {
    Stream<T> process(Stream<T> input);

    default Stream<T> process(Collection<T> input) {
        return process(input.stream());
    }
}
