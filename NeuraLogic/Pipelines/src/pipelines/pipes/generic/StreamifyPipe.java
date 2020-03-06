package pipelines.pipes.generic;

import pipelines.Pipe;
import settings.Settings;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class StreamifyPipe<S> extends Pipe<S, Stream<S>> {
    private static final Logger LOG = Logger.getLogger(StreamifyPipe.class.getName());

    public StreamifyPipe() {
        super("StreamifyPipe");
    }

    protected StreamifyPipe(String id, Settings settings) {
        super(id, settings);
    }

    @Override
    public Stream<S> apply(S template) {
        return Stream.of(template);
    }
}
