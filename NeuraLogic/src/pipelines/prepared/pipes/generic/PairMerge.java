package pipelines.prepared.pipes.generic;

import ida.utils.tuples.Pair;
import pipelines.Merge;

import java.util.logging.Logger;

public class PairMerge<I1,I2> extends Merge<I1,I2,Pair<I1,I2>> {
    private static final Logger LOG = Logger.getLogger(PairMerge.class.getName());

    public PairMerge(){
        super("PairMerge");
    }
    public PairMerge(String id) {
        super(id);
    }

    @Override
    protected Pair<I1, I2> merge(I1 input1, I2 input2) {
        return new Pair<>(input1,input2);
    }
}
