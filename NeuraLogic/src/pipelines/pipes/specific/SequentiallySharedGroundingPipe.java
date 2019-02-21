package pipelines.pipes.specific;

import grounding.GroundTemplate;
import grounding.Grounder;
import grounding.GroundingSample;
import pipelines.Pipe;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class SequentiallySharedGroundingPipe extends Pipe<Stream<GroundingSample>, Stream<GroundingSample>> {
    private static final Logger LOG = Logger.getLogger(SequentiallySharedGroundingPipe.class.getName());

    Grounder grounder;

    GroundTemplate stored = null;

    public SequentiallySharedGroundingPipe(Grounder grounder) {
        super("SequentiallySharedGroundingPipe");
        this.grounder = grounder;
    }

    @Override
    public Stream<GroundingSample> apply(Stream<GroundingSample> groundingSampleStream) {
        if (groundingSampleStream.isParallel()) {
            LOG.severe("Sequential sharing in a parallel grounding stream.");
            groundingSampleStream.sequential();
        }
        return groundingSampleStream.map(gs -> {
            if (gs.grounding.getGroundTemplate() == null || !gs.groundingComplete) {
                gs.grounding.setGroundTemplate(grounder.groundRulesAndFacts(gs.query.evidence, gs.template, stored));  //todo test for case with multiple queries on 1 example with sequential sharing (do we still increment against the last query here?)
            }
            return gs;
        });
    }
}
