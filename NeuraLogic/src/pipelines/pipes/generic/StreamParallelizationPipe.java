package pipelines.pipes.generic;

import pipelines.Pipe;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class StreamParallelizationPipe<I,O> extends Pipe<Stream<I>, Stream<O>> {
    private static final Logger LOG = Logger.getLogger(StreamParallelizationPipe.class.getName());

    public StreamParallelizationPipe() {
        super("ParallelizationPipe");
    }

    @Override
    public Stream<O> apply(Stream<I> iStream) {
        return null;
    }
}
