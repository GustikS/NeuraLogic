package pipeline.bulding;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.LearningSample;
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

    public class NormalLearningBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LearningSample>>,Pair<Template,Results>> {

        public NormalLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Template, Stream<LearningSample>>, Pair<Template, Results>> buildPipeline(Pair<Template, Stream<LearningSample>> sourceType) {
            GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
        }
    }

    public class StructureLearningBuilder extends AbstractPipelineBuilder<Stream<LearningSample>,Pair<Template,Results>> {

        public StructureLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Stream<LearningSample>, Pair<Template, Results>> buildPipeline(Stream<LearningSample> sourceType) {
            return null;
        }
    }

}