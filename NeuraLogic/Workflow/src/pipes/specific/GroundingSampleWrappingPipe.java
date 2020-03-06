package pipes.specific;

import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundingSample;
import pipelines.Pipe;
import settings.Settings;
import utils.generic.Pair;

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
        Stream<GroundingSample> groundingSampleStream = templateStreamPair.s.map(sample -> {
            if (sample.query.evidence == null) {
                LOG.severe("Query-Example mismatch: No example evidence was matched for this query: #" + sample.query.position + ":" + sample.query);
                System.exit(6);
            }
            GroundingSample groundingSample = new GroundingSample(sample, templateStreamPair.r);
            if (settings.groundingMode == Settings.GroundingMode.GLOBAL || settings.groundingMode == Settings.GroundingMode.SEQUENTIAL) {
                groundingSample.groundingWrap = lastGroundingWrap;
            }
            if (sample.query.evidence.equals(lastGroundingWrap.getExample())) {
                groundingSample.groundingComplete = true;
            } else {
                lastGroundingWrap.setExample(sample.query.evidence);
                groundingSample.groundingComplete = false;
            }
            return groundingSample;
        });
        return groundingSampleStream;
    }
}
