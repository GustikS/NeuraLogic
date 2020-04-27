package cz.cvut.fel.ida.pipelines.pipes.generic;

import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;

import java.util.function.Function;
import java.util.logging.Logger;

public class LambdaPipe<I,O> extends Pipe<I,O> {
    private static final Logger LOG = Logger.getLogger(LambdaPipe.class.getName());
    private Function<I, O> function;

    public LambdaPipe(String id, Function<I,O> function, Settings settings) {
        super(id, settings);
        this.function = function;
    }

    @Override
    public O apply(I t) throws Exception {
        return this.function.apply(t);
    }
}
