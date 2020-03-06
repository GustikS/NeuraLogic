package pipelines.pipes.generic;

import pipelines.Pipe;
import utils.generic.Pair;

import java.util.logging.Logger;

public class FirstFromPairPipe<R,S> extends Pipe<Pair<R,S>,R> {
    private static final Logger LOG = Logger.getLogger(FirstFromPairPipe.class.getName());

    public FirstFromPairPipe(){
        super("FirstFromPairPipe");
    }

    public FirstFromPairPipe(String id) {
        super(id);
    }

    @Override
    public R apply(Pair<R, S> rsPair) {
        return rsPair.r;
    }
}
