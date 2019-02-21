package pipelines.pipes.specific;

import grounding.Grounder;
import grounding.GroundingSample;
import pipelines.Pipe;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
        List<GroundingSample> groundingSampleList = groundingSampleStream.collect(Collectors.toList());
        groundingSampleList = grounder.globalGroundingSample(groundingSampleList);
        return groundingSampleList.stream();
    }
}
