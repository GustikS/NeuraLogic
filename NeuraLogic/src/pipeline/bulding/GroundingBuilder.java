package pipeline.bulding;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.LearningSample;
import pipeline.Pipeline;
import settings.Settings;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class GroundingBuilder extends AbstractPipelineBuilder<Pair<Stream<LearningSample>,Template>,Stream<NeuralSample>> {
    private static final Logger LOG = Logger.getLogger(GroundingBuilder.class.getName());

    public GroundingBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Pair<Stream<LearningSample>, Template>, Stream<NeuralSample>> buildPipeline(Pair<Stream<LearningSample>, Template> sourceType) {
        return null;
    }
}
