package pipeline.bulding;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import constructs.example.LogicSample;
import pipeline.Pipeline;
import pipeline.prepared.pipes.*;
import settings.Settings;
import training.NeuralModel;
import training.NeuralSample;
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
        public Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Template, Results>> buildPipeline() {
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Template, Results>> learningPipeline = new Pipeline<>("NormalLearningPipeline");

            FirstFromPairExtractionBranch<Template, Stream<LogicSample>> extractionBranch = new FirstFromPairExtractionBranch<>("ExtractTemplate");
            learningPipeline.registerStart(extractionBranch);

            GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
            Pipeline<Pair<Template,Stream<LogicSample>>, Stream<NeuralSample>> groundingPipeline = learningPipeline.register(groundingBuilder.buildPipeline());
            extractionBranch.output1 = groundingPipeline;

            TemplateToNeuralBranch templateToNeuralBranch = new TemplateToNeuralBranch("TemplateToNeuralBranch");
            learningPipeline.register(templateToNeuralBranch);
            extractionBranch.output2 = templateToNeuralBranch;

            PairMerge<NeuralModel, Stream<NeuralSample>> pairMerge = new PairMerge<>("NeuralModelSamplesMerge");

            learningPipeline.register(pairMerge);

            NeuralTrainingBuilder neuralTrainingBuilder = new NeuralTrainingBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> pairPairPipeline = learningPipeline.register(neuralTrainingBuilder.buildPipeline());
            pairMerge.output = pairPairPipeline;

            PairBranch<NeuralModel,Results> pairBranch = new PairBranch<>("PairBranch");
            learningPipeline.register(pairBranch);
            pairPairPipeline.output = pairBranch;

            NeuralToTemplateMerge neuralToTemplateMerge = new NeuralToTemplateMerge("NeuralToTemplateMerge");
            pairBranch.output1

            PairMerge<Template, Results> pairMerge2 = new PairMerge<>("TemplateResultsMerge");
            learningPipeline.register(pairMerge2);
        }
    }

    public class StructureLearningBuilder extends AbstractPipelineBuilder<Stream<LogicSample>,Pair<Template,Results>> {

        public StructureLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Stream<LogicSample>, Pair<Template, Results>> buildPipeline() {
            return null;
        }
    }

}