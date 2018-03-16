package pipelines;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class GenericMerge<I, O> implements Supplier<O> {
    private static final Logger LOG = Logger.getLogger(GenericMerge.class.getName());

    List<Supplier<I>> inputs;
    Consumer<O> output;

    public String ID;

    /**
     * Self computation (possibly parallel) and invocation of successor Consumer of O
     *
     * @param outputFromInputPipes
     */
    public void accept(List<I> outputFromInputPipes) {
        O merging = merge(outputFromInputPipes);
        output.accept(merging);
    }

    protected abstract O merge(List<I> outputFromInputPipes);

}