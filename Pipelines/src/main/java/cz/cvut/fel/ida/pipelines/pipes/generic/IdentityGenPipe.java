package cz.cvut.fel.ida.pipelines.pipes.generic;

import cz.cvut.fel.ida.pipelines.Pipe;

import java.util.logging.Logger;

public class IdentityGenPipe<T> extends Pipe<T, T> {
    private static final Logger LOG = Logger.getLogger(IdentityGenPipe.class.getName());

    public IdentityGenPipe() {
        super("IdentityGenPipe");
    }

    public IdentityGenPipe(String id) {
        super(id);
    }

    @Override
    public T apply(T sources) {
        return sources;
    }
}
