package pipeline;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * This method must be terminating, since it is not possible to fork streams,
 * hence the return value is List to signify this
 */
public abstract class MultiBranch<I, O> extends Pipe<I, Stream<O>> {
    boolean parallelBranching = true;

    private static final Logger LOG = Logger.getLogger(MultiBranch.class.getName());

    protected MultiBranch(String id) {
        super(id);
    }

    /**
     * Self computation and Parallel invocation of successor Consumers of O
     *
     * @param outputFromInputPipe
     */
    public void accept(I outputFromInputPipe) {
        outputReady = apply(outputFromInputPipe);
        if (this.output != null)
            if (parallelBranching)
                output.accept(outputReady.parallel());
            else
                output.accept(outputReady);
    }
}
