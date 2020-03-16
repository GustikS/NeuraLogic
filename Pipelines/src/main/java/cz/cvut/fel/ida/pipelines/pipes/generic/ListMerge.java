package cz.cvut.fel.ida.pipelines.pipes.generic;

import cz.cvut.fel.ida.pipelines.MultiMerge;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.logging.Logger;

public class ListMerge<I> extends MultiMerge<I, List<I>> {
    private static final Logger LOG = Logger.getLogger(ListMerge.class.getName());


    public ListMerge(int count, Settings settings) {
        super("ListMerge", count, settings);
    }

    @Override
    protected List<I> merge(List<I> inputs) {
        return inputs;
    }
}