package pipes.specific;

import grounding.GroundingSample;
import pipelines.Pipe;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class SupervisedGroundTemplatePruningPipe extends Pipe<Stream<GroundingSample>, Stream<GroundingSample>> {
    private static final Logger LOG = Logger.getLogger(SupervisedGroundTemplatePruningPipe.class.getName());

    public SupervisedGroundTemplatePruningPipe() {
        super("SupervisedGroundTemplatePruning");
    }

    @Override
    public Stream<GroundingSample> apply(Stream<GroundingSample> groundingSampleStream) {
        return groundingSampleStream.map(p -> {
            p.groundingWrap.setGroundTemplate(p.groundingWrap.getGroundTemplate().prune(p.query));
            return p;
        });
    }
}
