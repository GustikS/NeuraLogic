package pipeline;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * This block may be non-terminating - it is possible to return merging of streams as a stream,
 * hence the return value is open - it can be a Stream, List, or any Object
 */
public abstract class MultiMerge<I, O> extends Pipe<Stream<I>, O> {
    private static final Logger LOG = Logger.getLogger(MultiMerge.class.getName());

    protected MultiMerge(String id) {
        super(id);
    }

    /**
     * Self computation (possibly parallel) and invocation of successor Consumer of O
     *
     * @param allInputs
     */
    public void accept(Stream<I> allInputs) {
        outputReady = apply(allInputs);
        if (this.output != null) {
            this.output.accept(outputReady);
        }
    }
}