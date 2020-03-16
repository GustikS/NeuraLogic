package cz.cvut.fel.ida.pipelines;

import cz.cvut.fel.ida.pipelines.pipes.generic.IdentityGenPipe;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class Branch<I, O1, O2> extends Block implements ConnectBefore<I> {
    private static final Logger LOG = Logger.getLogger(Branch.class.getName());

    public ConnectAfter<I> input;
    public IdentityGenPipe<O1> output1;
    public IdentityGenPipe<O2> output2;

    Pair<O1, O2> outputReady;

    public Branch(String id) {
        ID = id;
        output1 = new IdentityGenPipe<>(id + "Output1");
        output2 = new IdentityGenPipe<>(id + "Output2");
    }

    public void accept(I outputFromInputPipe) {
        LOG.finer("Entering: " + ID);
        outputReady = branch(outputFromInputPipe);
        output1.accept(outputReady.r);
        output2.accept(outputReady.s);
    }

    protected abstract Pair<O1, O2> branch(I outputFromInputPipe);

    @Override
    public ConnectAfter<I> getInput() {
        return input;
    }

    @Override
    public void setInput(ConnectAfter<I> prev) {
        input = prev;
    }

    public ConnectBefore<O1> connectAfterL(ConnectBefore<O1> next) {
        return output1.connectAfter(next);
    }

    public <X> Pipeline<O1, X> connectAfterL(Pipeline<O1, X> next) {
        return output1.connectAfter(next);
    }

    public ConnectBefore<O2> connectAfterR(ConnectBefore<O2> next) {
        return output2.connectAfter(next);
    }

    public <X> Pipeline<O2, X> connectAfterR(Pipeline<O2, X> next) {
        return output2.connectAfter(next);
    }

    public List<Branch<I, O1, O2>> parallel(int count) {
        List<Branch<I, O1, O2>> copies = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            copies.add(new Branch<I, O1, O2>(this.ID + i) {
                @Override
                protected Pair<O1, O2> branch(I i1) {
                    return Branch.this.branch(i1);
                }
            });
        }
        return copies;
    }

    ;

    public static <T, A extends Branch<?, T, ?>, B extends ConnectBefore<T>> void connectAfterL(List<A> branches, List<B> next) {
        if (branches.size() != next.size()) {
            LOG.severe("The 2 Lists of branches and pipes provided cannot be connected with different sizes!");
        }
        for (int i = 0; i < branches.size(); i++) {
            branches.get(i).connectAfterL(next.get(i));
        }
    }

    public static <T, A extends Branch<?, ?, T>, B extends ConnectBefore<T>> void connectAfterR(List<A> branches, List<B> next) {
        if (branches.size() != next.size()) {
            LOG.severe("The 2 Lists of branches and pipes provided cannot be connected with different sizes!");
        }
        for (int i = 0; i < branches.size(); i++) {
            branches.get(i).connectAfterR(next.get(i));
        }
    }

    public static <T> void connectBefore(List<Branch<T, ?, ?>> branches, List<ConnectAfter<T>> next) {
        if (branches.size() != next.size()) {
            LOG.severe("The 2 Lists of branches and pipes provided cannot be connected with different sizes!");
        }
        for (int i = 0; i < branches.size(); i++) {
            branches.get(i).connectBefore(next.get(i));
        }
    }

}