package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.logic.grounding.Grounder;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.pipelines.Pipe;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class StandardGroundingPipe extends Pipe<Stream<GroundingSample>, Stream<GroundingSample>> {
    private static final Logger LOG = Logger.getLogger(StandardGroundingPipe.class.getName());

    Grounder grounder;

    public StandardGroundingPipe(Grounder grounder) {
        super("StandardGroundingPipe");
        this.grounder = grounder;
    }

    @Override
    public Stream<GroundingSample> apply(Stream<GroundingSample> groundingSampleStream) {
        return groundingSampleStream.map(gs -> {
            if (gs.groundingWrap.getGroundTemplate() == null || !gs.groundingComplete) {
                gs.groundingWrap.setGroundTemplate(grounder.groundRulesAndFacts(gs.query.evidence, gs.template));
            }
//            else if (!gs.groundingComplete) {     // this is in the sequentiallyShared pipe?!
//                gs.groundingWrap.setGroundTemplate(grounder.groundRulesAndFacts(gs.query.evidence, gs.template, gs.groundingWrap.getGroundTemplate()));
////                gs.groundingWrap.setNeuronMaps(gs.cache.copy());    //todo next check in some sequentially or partially shared setting
//                return gs;
//            }
            return gs;
        });
    }
}
