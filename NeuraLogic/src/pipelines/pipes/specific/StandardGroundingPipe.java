package pipelines.pipes.specific;

import grounding.Grounder;
import grounding.GroundingSample;
import pipelines.Pipe;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class StandardGroundingPipe extends Pipe<Stream<GroundingSample>, Stream<GroundingSample>> {
    private static final Logger LOG = Logger.getLogger(StandardGroundingPipe.class.getName());

    Grounder grounder;

    public StandardGroundingPipe(Grounder grounder) {
        super("StandardGroundingPipe");
    }

    @Override
    public Stream<GroundingSample> apply(Stream<GroundingSample> groundingSampleStream) {
        return groundingSampleStream.map(gs -> {
            if (gs.grounding.getGroundTemplate() == null) {
                gs.grounding.setGroundTemplate(grounder.groundRulesAndFacts(gs.query.evidence, gs.template));
            } else if (!gs.groundingComplete) {
                gs.grounding.setGroundTemplate(grounder.groundRulesAndFacts(gs.query.evidence, gs.template, gs.grounding.getGroundTemplate()));
                return gs;
            }
            return gs;
        });
    }
}
