package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import networks.evaluation.results.Results;
import pipelines.Pipeline;
import pipelines.RecurrentPipe;
import pipelines.prepared.pipes.generic.FirstFromPairExtractionBranch;
import pipelines.prepared.pipes.generic.PairMerge;
import pipelines.prepared.pipes.specific.TemplateToNeuralPipe;
import settings.Settings;
import training.NeuralModel;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingBuilder {
    private static final Logger LOG = Logger.getLogger(TrainingBuilder.class.getName());
    private final Settings settings;

    NeuralLearningBuilder neuralLearningBuilder;

    public TrainingBuilder(Settings settings) {
        this.settings = settings;
    }

    public class LogicLearningBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> {

        public LogicLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> buildPipeline() {
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> learningPipeline = new Pipeline<>("NormalLearningPipeline");

            FirstFromPairExtractionBranch<Template, Stream<LogicSample>> extractionBranch = learningPipeline.registerStart(new FirstFromPairExtractionBranch<>());

            GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
            Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> groundingPipeline = learningPipeline.register(groundingBuilder.buildPipeline());

            TemplateToNeuralPipe templateToNeuralPipe = learningPipeline.register(new TemplateToNeuralPipe());

            PairMerge<NeuralModel, Stream<NeuralSample>> pairMerge = learningPipeline.register(new PairMerge<>());

            neuralLearningBuilder = new NeuralLearningBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> trainingPipeline = learningPipeline.registerEnd(neuralLearningBuilder.buildPipeline());

            extractionBranch.connectAfterL(groundingPipeline);
            extractionBranch.connectAfterR(templateToNeuralPipe);

            pairMerge.connectBeforeL(templateToNeuralPipe);
            pairMerge.connectBeforeR(groundingPipeline);

            pairMerge.connectAfter(trainingPipeline);

            return learningPipeline;
        }

        /**
         * Just a shortcut to NeuralLearningBuilder
         * @return
         */
        public Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> buildNeuralPipeline() {
            return neuralLearningBuilder.buildPipeline();
        }
    }

    public class StructureLearningBuilder extends AbstractPipelineBuilder<Stream<LogicSample>, Pair<Pair<Template, NeuralModel>, Results>> {

        public StructureLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Stream<LogicSample>, Pair<Pair<Template, NeuralModel>, Results>> buildPipeline() {
            //TODO introduce recurrent Pipe - 2 inputs, 2 outputs with internal generic counter/while condition, switching from recurrent output->input to final output
            RecurrentPipe recurrentPipe = new RecurrentPipe() {
                @Override
                public int hashCode() {
                    return super.hashCode();
                }
            };
            return null;
        }
    }

    public class NeuralLearningBuilder extends AbstractPipelineBuilder<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>>{

        public NeuralLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> buildPipeline() {
            return null;
        }
    }

}