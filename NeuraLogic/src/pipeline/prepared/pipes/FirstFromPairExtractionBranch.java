package pipeline.prepared.pipes;

import ida.utils.tuples.Pair;
import pipeline.Branch;

import java.util.logging.Logger;

public class FirstFromPairExtractionBranch<O1,O2> extends Branch<Pair<O1,O2>,Pair<O1,O2>,O1> {
    private static final Logger LOG = Logger.getLogger(FirstFromPairExtractionBranch.class.getName());

    public FirstFromPairExtractionBranch(String id) {
        super(id);
    }

    @Override
    protected Pair<Pair<O1, O2>, O1> branch(Pair<O1, O2> outputFromInputPipe) {
        return new Pair<>(outputFromInputPipe, outputFromInputPipe.r);
    }
}
