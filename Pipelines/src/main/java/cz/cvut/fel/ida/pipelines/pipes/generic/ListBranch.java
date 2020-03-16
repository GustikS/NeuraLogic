package cz.cvut.fel.ida.pipelines.pipes.generic;

import cz.cvut.fel.ida.pipelines.MultiBranch;
import cz.cvut.fel.ida.setup.Settings;

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