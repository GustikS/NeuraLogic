package pipelines;

import utils.Utilities;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class GenericBranch<I,O> implements Consumer<I> {
    private static final Logger LOG = Logger.getLogger(GenericBranch.class.getName());

    Supplier<I> input;
    List<Consumer<O>> outputs;

    public String ID;

    /**
     * Self computation and Parallel invocation of successor Consumers of O
     * @param outputFromInputPipe
     */
    public void accept(I outputFromInputPipe) {
        List<O> branching = branch(outputFromInputPipe);
        Utilities.zipLists(branching, outputs).parallelStream().forEach(pair -> pair.s.accept(pair.r));
    }

    protected abstract List<O> branch(I outputFromInputPipe);

}