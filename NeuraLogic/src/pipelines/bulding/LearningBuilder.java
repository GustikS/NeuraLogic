package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import pipelines.Pipeline;
import pipelines.prepared.pipes.specific.TemplateToNeuralPipe;
import pipelines.prepared.pipes.generic.FirstFromPairExtractionBranch;
import pipelines.prepared.pipes.generic.PairMerge;
import settings.Settings;
import training.NeuralModel;
import training.NeuralSample;
import networks.evaluation.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class LearningBuilder {
    private static final Logger LOG = Logger.getLogger(LearningBuilder.class.getName());
    private final Settings settings;

    public LearningBuilder(Settings settings) {
        this.settings = settings;
    }

    public class NormalLearningBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>,Pair<NeuralModel,Results>> {

        public NormalLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> buildPipeline() {
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> learningPipeline = new Pipeline<>("NormalLearningPipeline");

            FirstFromPairExtractionBranch<Template, Stream<LogicSample>> extractionBranch = learningPipeline.registerStart(new FirstFromPairExtractionBranch<>());

            GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
            Pipeline<Pair<Template,Stream<LogicSample>>, Stream<NeuralSample>> groundingPipeline = learningPipeline.register(groundingBuilder.buildPipeline());

            TemplateToNeuralPipe templateToNeuralPipe = learningPipeline.register(new TemplateToNeuralPipe());

            PairMerge<NeuralModel, Stream<NeuralSample>> pairMerge = learningPipeline.register(new PairMerge<>());

            NeuralTrainingBuilder neuralTrainingBuilder = new NeuralTrainingBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> trainingPipeline = learningPipeline.register(neuralTrainingBuilder.buildPipeline());

            extractionBranch.connectAfterL(groundingPipeline);
            extractionBranch.connectAfterR(templateToNeuralPipe);

            pairMerge.connectBeforeL(templateToNeuralPipe);
            pairMerge.connectBeforeR(groundingPipeline);

            pairMerge.connectAfter(trainingPipeline);

            return learningPipeline;
        }
    }

    public class StructureLearningBuilder extends AbstractPipelineBuilder<Stream<LogicSample>,Pair<Template,Results>> {

        public StructureLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Stream<LogicSample>, Pair<Template, Results>> buildPipeline() {
            //TODO introduce recurrent Pipe - 2 inputs, 2 outputs with internal generic counter/while condition, switching from recurrent output->input to final output
            return null;
        }
    }

}