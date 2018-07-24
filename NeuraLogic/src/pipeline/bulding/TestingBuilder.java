package pipeline.bulding;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.LearningSample;
import pipeline.Pipeline;
import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TestingBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LearningSample>>,Results> {
    private static final Logger LOG = Logger.getLogger(TestingBuilder.class.getName());

    public TestingBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Pair<Template, Stream<LearningSample>>, Results> buildPipeline(Pair<Template, Stream<LearningSample>> sourceType) {
        return null;
    }

    public void buildPipeline(Sources sources) {

    }
}
