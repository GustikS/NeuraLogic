package pipeline;

import ida.utils.tuples.Pair;

import java.util.logging.Logger;

public  class Branch<I, O1, O2> implements ConnectBefore<I>, ConnectAfter {
    private static final Logger LOG = Logger.getLogger(Branch.class.getName());

    public ConnectAfter<I> input;
    public ConnectBefore<O1> output1;
    public ConnectBefore<O2> output2;

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

    @Override
    public ConnectAfter<I> getInput() {
        return input;
    }

    @Override
    public void setInput(ConnectAfter<I> prev) {
        input = prev;
    }

    @Override
    public ConnectBefore getOutput() {
        return null;
    }

    @Override
    public void setOutput(ConnectBefore prev) {

    }

    @Override
    public Object get() {
        return null;
    }
}