package pipelines.pipes.specific;

import grounding.GroundingSample;
import pipelines.Pipe;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class SupervisedGroundTemplatePruning extends Pipe<Stream<GroundingSample>, Stream<GroundingSample>> {
    private static final Logger LOG = Logger.getLogger(SupervisedGroundTemplatePruning.class.getName());

    public SupervisedGroundTemplatePruning() {
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
