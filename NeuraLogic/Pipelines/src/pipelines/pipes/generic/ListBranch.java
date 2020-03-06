package pipelines.pipes.generic;

import pipelines.MultiBranch;
import settings.Settings;

import java.util.List;
import java.util.logging.Logger;

public class ListBranch<I> extends MultiBranch<List<I>, I> {
    private static final Logger LOG = Logger.getLogger(ListBranch.class.getName());

    public ListBranch(int count, Settings settings) {
        super("ListBranch", count, settings);
    }

    @Override
    protected List<I> branch(List<I> outputFromInputPipe) {
        return outputFromInputPipe;
    }
}