package pipelines.pipes.generic;

import pipelines.MultiBranch;

import java.util.List;
import java.util.logging.Logger;

public class ListBranch<I> extends MultiBranch<List<I>, I> {
    private static final Logger LOG = Logger.getLogger(ListBranch.class.getName());

    public ListBranch(int count) {
        super("ListBranch", count);
    }

    protected ListBranch(String id, int count) {
        super(id, count);
    }

    @Override
    protected List<I> branch(List<I> outputFromInputPipe) {
        return outputFromInputPipe;
    }
}