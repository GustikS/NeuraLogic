package pipelines.prepared.pipes.generic;

import ida.utils.tuples.Pair;
import pipelines.Pipe;

import java.util.logging.Logger;

public class SecondFromPairPipe<R,S> extends Pipe<Pair<R,S>,S> {
    private static final Logger LOG = Logger.getLogger(SecondFromPairPipe.class.getName());

    public SecondFromPairPipe(){
        super("SecondFromPairPipe");
    }

    protected SecondFromPairPipe(String id) {
        super(id);
    }

    @Override
    public S apply(Pair<R, S> rsPair) {
        return rsPair.s;
    }
}
