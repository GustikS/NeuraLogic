package pipelines.pipes.generic;

import pipelines.MultiMerge;
import settings.Settings;

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