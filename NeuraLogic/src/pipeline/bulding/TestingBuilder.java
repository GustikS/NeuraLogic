package pipeline.bulding;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import constructs.example.LogicSample;
import pipeline.Pipeline;
import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TestingBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>,Results> {
    private static final Logger LOG = Logger.getLogger(TestingBuilder.class.getName());

    public TestingBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Pair<Template, Stream<LogicSample>>, Results> buildPipeline(Pair<Template, Stream<LogicSample>> sourceType) {
        return null;
    }

    public void buildPipeline(Sources sources) {

    }
}
