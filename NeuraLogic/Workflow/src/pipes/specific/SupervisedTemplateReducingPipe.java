package pipes.specific;

import grounding.GroundingSample;
import pipelines.Pipe;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class SupervisedTemplateReducingPipe extends Pipe<Stream<GroundingSample>, Stream<GroundingSample>> {
    private static final Logger LOG = Logger.getLogger(SupervisedTemplateReducingPipe.class.getName());

    public SupervisedTemplateReducingPipe() {
        super("SupervisedTemplateReducingPipe");
    }


    @Override
    public Stream<GroundingSample> apply(Stream<GroundingSample> groundingSampleStream) {
        return groundingSampleStream.map(sample -> {
            sample.template = sample.template.prune(sample.query);
            sample.groundingComplete = false;
            return sample;
        });
    }
}
