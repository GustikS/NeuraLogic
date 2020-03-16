package cz.cvut.fel.ida.pipelines.pipes.generic;

import cz.cvut.fel.ida.pipelines.MultiBranch;
import cz.cvut.fel.ida.setup.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DuplicateListBranch<I> extends MultiBranch<I,I> {
    private static final Logger LOG = Logger.getLogger(DuplicateListBranch.class.getName());

    public DuplicateListBranch(int count, Settings settings) {
        super("DuplicateListBranch", count, settings);
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
