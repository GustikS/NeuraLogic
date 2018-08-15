package pipelines.pipes.generic;

import pipelines.MultiMerge;

import java.util.List;
import java.util.logging.Logger;

public class ListMerge<I> extends MultiMerge<I, List<I>> {
    private static final Logger LOG = Logger.getLogger(ListMerge.class.getName());


    public ListMerge(int count) {
        super("ListMerge", count);
    }

    protected ListMerge(String id, int count) {
        super(id, count);
    }

    @Override
    protected List<I> merge(List<I> inputs) {
        return inputs;
    }
}