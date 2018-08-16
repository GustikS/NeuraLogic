package pipelines.bulding;

import constructs.template.Template;
import grounding.Grounder;
import ida.utils.tuples.Pair;
import constructs.example.LogicSample;
import pipelines.Pipeline;
import settings.Settings;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class GroundingBuilder extends AbstractPipelineBuilder<Pair<Template,Stream<LogicSample>>,Stream<NeuralSample>> {
    private static final Logger LOG = Logger.getLogger(GroundingBuilder.class.getName());

    Grounder grounder;

    public GroundingBuilder(Settings settings) {
        super(settings);
        grounder = Grounder.getGrounder(settings);
    }

    @Override
    public Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> buildPipeline() {
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> pipeline = new Pipeline<>("GroundingPipeline");

        return null;
    }

}
