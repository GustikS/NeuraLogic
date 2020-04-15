package cz.cvut.fel.ida.pipelines.pipes.generic;

import cz.cvut.fel.ida.pipelines.Merge;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;

public class PairMerge<I1,I2> extends Merge<I1,I2, Pair<I1,I2>> {
    private static final Logger LOG = Logger.getLogger(PairMerge.class.getName());

    public PairMerge(){
        super("PairMerge", null);
    }
    public PairMerge(String id) {
        super(id, null);
    }

    @Override
    protected Pair<I1, I2> merge(I1 input1, I2 input2) {
        return new Pair<>(input1,input2);
    }
}
