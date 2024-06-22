package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.grounding.Grounder;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.pipelines.Pipe;

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
            if (gs.groundingWrap.getGroundTemplate() == null || !gs.groundingComplete) {
                gs.groundingWrap.setGroundTemplate(grounder.groundRulesAndFacts(gs.query.evidence, gs.template, stored));  //todo test for case with multiple queries on 1 example with sequential sharing (do we still increment against the last query here?)
//                gs.groundingWrap.setNeuronMaps(gs.cache.copy());    //todo next check in some sequentially or partially shared setting
            }
            return gs;
        });
    }
}
