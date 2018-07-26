package pipeline.prepared.pipes;

import pipeline.Pipe;

import java.util.logging.Logger;

public class IdentityGenPipe<T> extends Pipe<T, T> {
    private static final Logger LOG = Logger.getLogger(IdentityGenPipe.class.getName());

    public IdentityGenPipe(String id) {
        super(id);
    }

    @Override
    public T apply(T sources) {
        return sources;
    }
}
