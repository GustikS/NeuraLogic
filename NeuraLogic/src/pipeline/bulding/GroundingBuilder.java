package pipeline.bulding;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import constructs.example.LogicSample;
import pipeline.Pipeline;
import settings.Settings;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class GroundingBuilder extends AbstractPipelineBuilder<Pair<Stream<LogicSample>,Template>,Stream<NeuralSample>> {
    private static final Logger LOG = Logger.getLogger(GroundingBuilder.class.getName());

    public GroundingBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Pair<Stream<LogicSample>, Template>, Stream<NeuralSample>> buildPipeline(Pair<Stream<LogicSample>, Template> sourceType) {
        return null;
    }
}
