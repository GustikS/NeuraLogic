package pipelines.prepared.pipes.generic;

import pipelines.MultiBranch;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DuplicateListBranch<I> extends MultiBranch<I,I> {
    private static final Logger LOG = Logger.getLogger(DuplicateListBranch.class.getName());

    public DuplicateListBranch(int count) {
        super("DuplicateListBranch", count);
    }

    protected DuplicateListBranch(String id, int count) {
        super(id, count);
    }

    @Override
    protected List<I> branch(I i) {
        List<I> copies = new ArrayList<>(outputs.size());
        for (int j = 0; j < outputs.size(); j++) {
            copies.add(i);
        }
        return copies;
    }
}
