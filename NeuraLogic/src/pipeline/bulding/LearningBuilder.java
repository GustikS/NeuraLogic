package pipeline.bulding;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import constructs.example.LogicSample;
import pipeline.Pipeline;
import settings.Settings;
import training.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class LearningBuilder {
    private static final Logger LOG = Logger.getLogger(LearningBuilder.class.getName());
    private final Settings settings;

    public LearningBuilder(Settings settings) {
        this.settings = settings;
    }

    public class NormalLearningBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>,Pair<Template,Results>> {

        public NormalLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Template, Results>> buildPipeline(Pair<Template, Stream<LogicSample>> sourceType) {
            GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
        }
    }

    public class StructureLearningBuilder extends AbstractPipelineBuilder<Stream<LogicSample>,Pair<Template,Results>> {

        public StructureLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Stream<LogicSample>, Pair<Template, Results>> buildPipeline(Stream<LogicSample> sourceType) {
            return null;
        }
    }

}