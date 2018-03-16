package pipelines;

import ida.utils.tuples.Pair;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class Branch<I, O1, O2> implements Consumer<I> {
    private static final Logger LOG = Logger.getLogger(Branch.class.getName());

    Supplier<I> input;
    Consumer<O1> output1;
    Consumer<O2> output2;

    public String ID;

    public Branch(String id) {
        ID = id;
    }

    public void accept(I outputFromInputPipe) {
        Pair<O1,O2> state =  branch(outputFromInputPipe);
        output1.accept(state.r);
        output2.accept(state.s);
    }

    protected abstract Pair<O1,O2> branch(I outputFromInputPipe);

}