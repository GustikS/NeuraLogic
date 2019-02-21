package pipelines.pipes.specific;

import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundingSample;
import ida.utils.tuples.Pair;
import pipelines.Pipe;
import settings.Settings;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class GroundingSampleWrappingPipe extends Pipe<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> {
    private static final Logger LOG = Logger.getLogger(GroundingSampleWrappingPipe.class.getName());
    private final Settings settings;

    public GroundingSampleWrappingPipe(Settings settings) {
        super("GroundingSampleWrappingPipe");
        this.settings = settings;
    }

    @Override
    public Stream<GroundingSample> apply(Pair<Template, Stream<LogicSample>> templateStreamPair) {
        if (templateStreamPair.s.isParallel()) {
            LOG.warning("Samples come in parallel into grounder already, this may have negative effect on shared grounding."); //can be: https://stackoverflow.com/questions/29216588/how-to-ensure-order-of-processing-in-java8-streams
            if (!settings.oneQueryPerExample)
                templateStreamPair.s = templateStreamPair.s.sequential();
        }
        final GroundingSample.Wrap lastGroundingWrap = new GroundingSample.Wrap(null);
        templateStreamPair.s.map(sample -> {
            GroundingSample groundingSample = new GroundingSample(sample, templateStreamPair.r);
            if (sample.query.evidence.equals(lastGroundingWrap.getExample())) {
                groundingSample.grounding = lastGroundingWrap;
                groundingSample.groundingComplete = true;
            } else {
                lastGroundingWrap.setExample(sample.query.evidence);
            }
            return groundingSample;
        });
        return null;
    }
}
