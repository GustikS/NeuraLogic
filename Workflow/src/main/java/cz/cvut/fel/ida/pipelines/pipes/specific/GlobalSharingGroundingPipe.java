package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.utils.generic.Utilities;
import cz.cvut.fel.ida.logic.grounding.Grounder;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.pipelines.Pipe;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GlobalSharingGroundingPipe extends Pipe<Stream<GroundingSample>, Stream<GroundingSample>> {
    private static final Logger LOG = Logger.getLogger(GlobalSharingGroundingPipe.class.getName());

    Grounder grounder;

    public GlobalSharingGroundingPipe(Grounder grounder) {
        super("GlobalSharingGroundingPipe");
        this.grounder = grounder;
    }

    @Override
    public Stream<GroundingSample> apply(Stream<GroundingSample> groundingSampleStream) {
        List<GroundingSample> groundingSampleList = Utilities.terminateSampleStream(groundingSampleStream);
        groundingSampleList = grounder.globalGroundingSample(groundingSampleList);
        return groundingSampleList.stream();
    }
}
