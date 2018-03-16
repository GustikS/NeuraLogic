package pipelines;

import java.util.logging.Logger;

public class SourceGenPipe<Sources> extends Pipe<Sources, Sources> {
    private static final Logger LOG = Logger.getLogger(SourceGenPipe.class.getName());

    SourceGenPipe(String id) {
        super(id);
    }

    @Override
    public Sources apply(Sources sources) {
        return sources;
    }
}