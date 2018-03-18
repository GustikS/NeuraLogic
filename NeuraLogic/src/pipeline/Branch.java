package pipeline;

import ida.utils.tuples.Pair;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class Branch<I, O1, O2> implements Consumer<I> {
    private static final Logger LOG = Logger.getLogger(Branch.class.getName());

    public Supplier<I> input;
    public Consumer<O1> output1;
    public Consumer<O2> output2;

    Pair<O1,O2> outputReady;

    public String ID;

    public Branch(String id) {
        ID = id;
    }

    public void accept(I outputFromInputPipe) {
        outputReady =  branch(outputFromInputPipe);
        output1.accept(outputReady.r);
        output2.accept(outputReady.s);
    }

    protected abstract Pair<O1,O2> branch(I outputFromInputPipe);

}