package pipeline;

import ida.utils.tuples.Pair;
import pipeline.prepared.pipes.generic.IdentityGenPipe;

import java.util.logging.Logger;

public abstract class Branch<I, O1, O2> implements ConnectBefore<I> {
    private static final Logger LOG = Logger.getLogger(Branch.class.getName());

    public ConnectAfter<I> input;
    public IdentityGenPipe<O1> output1;
    public IdentityGenPipe<O2> output2;

    Pair<O1,O2> outputReady;

    public String ID;

    public Branch(String id) {
        ID = id;
        output1 = new IdentityGenPipe<>(id + "Output1");
        output2 = new IdentityGenPipe<>(id + "Output2");
    }

    public void accept(I outputFromInputPipe) {
        outputReady =  branch(outputFromInputPipe);
        output1.accept(outputReady.r);
        output2.accept(outputReady.s);
    }

    protected abstract Pair<O1,O2> branch(I outputFromInputPipe);

    @Override
    public ConnectAfter<I> getInput() {
        return input;
    }

    @Override
    public void setInput(ConnectAfter<I> prev) {
        input = prev;
    }

    public ConnectBefore<O1> connectAfterL(ConnectBefore<O1> next){
        return output1.connectAfter(next);
    }

    public ConnectBefore<O2> connectAfterR(ConnectBefore<O2> next){
        return output2.connectAfter(next);
    }
}