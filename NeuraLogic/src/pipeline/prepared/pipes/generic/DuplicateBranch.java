package pipeline.prepared.pipes.generic;

import ida.utils.tuples.Pair;
import pipeline.Branch;

import java.util.logging.Logger;

public class DuplicateBranch<T> extends Branch<T,T,T> {
    private static final Logger LOG = Logger.getLogger(DuplicateBranch.class.getName());

    public DuplicateBranch(){
        super("DuplicateBranch");
    }

    public DuplicateBranch(String id) {
        super(id);
    }

    @Override
    protected Pair<T, T> branch(T outputFromInputPipe) {
        return new Pair<>(outputFromInputPipe,outputFromInputPipe);
    }
}
