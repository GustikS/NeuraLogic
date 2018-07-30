package pipeline.prepared.pipes;

import ida.utils.tuples.Pair;
import pipeline.Branch;

import java.util.logging.Logger;

public class PairBranch<O1,O2> extends Branch<Pair<O1,O2>,O1,O2> {
    private static final Logger LOG = Logger.getLogger(PairBranch.class.getName());

    public PairBranch(String id) {
        super(id);
    }

    @Override
    protected Pair<O1, O2> branch(Pair<O1, O2> outputFromInputPipe) {
        return outputFromInputPipe;
    }
}
