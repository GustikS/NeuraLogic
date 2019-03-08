package pipelines.pipes.generic;

import pipelines.Pipe;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class StreamParallelizationPipe<I> extends Pipe<Stream<I>, Stream<I>> {
    private static final Logger LOG = Logger.getLogger(StreamParallelizationPipe.class.getName());

    public StreamParallelizationPipe() {
        super("ParallelizationPipe");
    }

    @Override
    public Stream<I> apply(Stream<I> iStream) {
        return iStream.parallel();
    }
}
