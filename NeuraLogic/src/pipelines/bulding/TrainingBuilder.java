package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import networks.evaluation.results.Results;
import pipelines.Merge;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.RecurrentPipe;
import pipelines.prepared.pipes.generic.DuplicateBranch;
import pipelines.prepared.pipes.generic.FirstFromPairExtractionBranch;
import pipelines.prepared.pipes.generic.PairMerge;
import pipelines.prepared.pipes.specific.TemplateToNeuralPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import training.NeuralModel;
import training.NeuralSample;

import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingBuilder extends AbstractPipelineBuilder<Sources, Pair<Pair<Template, NeuralModel>,Results>> {
    private static final Logger LOG = Logger.getLogger(TrainingBuilder.class.getName());

    NeuralLearningBuilder neuralLearningBuilder;

    Sources sources;

    public TrainingBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Results>> buildPipeline(){
        return buildPipeline(sources);
    }


    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Results>> buildPipeline(Sources sources) {
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Results>> pipeline = new Pipeline<>("TrainingPipeline");
        DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>());
        Pipe<Sources, Source> sourcesSourcePipe = pipeline.register(new Pipe<Sources, Source>("SourcesSourcePipe") {
            @Override
            public Source apply(Sources sources) {
                return sources.train;
            }
        });
        duplicateBranch.connectAfterL(sourcesSourcePipe);
        SamplesProcessingBuilder trainingSamplesProcessor = new SamplesProcessingBuilder(settings, sources.train);
        Pipeline<Source, Stream<LogicSample>> sourcesStreamPipeline = pipeline.register(trainingSamplesProcessor.buildPipeline());
        sourcesSourcePipe.connectAfter(sourcesStreamPipeline);

        if (sources.templateProvided) {
            TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);
            Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());

            Pipe<Template, Optional<Template>> optionalPipe = pipeline.register(new Pipe<Template, Optional<Template>>("OptionalPipe") {
                @Override
                public Optional<Template> apply(Template template) {
                    return Optional.of(template);
                }
            });

            PairMerge<Optional<Template>, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());
            LogicTrainingBuilder logicTrainingBuilder = new LogicTrainingBuilder(settings);
            Pipeline<Pair<Optional<Template>, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Results>> pairPairPipeline = pipeline.registerEnd(logicTrainingBuilder.buildPipeline());

            sourcesTemplatePipeline.connectAfter(optionalPipe);
            optionalPipe.connectAfter(pairMerge.input1);

            duplicateBranch.connectAfterR(sourcesTemplatePipeline);
            pairMerge.connectBeforeR(sourcesStreamPipeline);
            pairMerge.connectAfter(pairPairPipeline);

        } else {
            Pipe<Sources, Optional<Template>> optionalPipe = pipeline.register(new Pipe<Sources, Optional<Template>>("OptionalEmptyPipe") {
                @Override
                public Optional<Template> apply(Sources sources) {
                    if (sources.templateProvided){
                        LOG.severe("Template should not have been provided - Sources mismatch!");
                    }
                    return Optional.empty();
                }
            });
        }
        return pipeline;
    }

    /**
     * TODO - in case that some of the samples did not ground succesfully, hack the pipeline and start StructureLearning in the current context
     */
    public class LogicTrainingBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> {

        public LogicTrainingBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> buildPipeline() {
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> pipeline = new Pipeline<>("NormalLearningPipeline");

            FirstFromPairExtractionBranch<Template, Stream<LogicSample>> extractionBranch = pipeline.registerStart(new FirstFromPairExtractionBranch<>());

            GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
            Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> groundingPipeline = pipeline.register(groundingBuilder.buildPipeline());

            TemplateToNeuralPipe templateToNeuralPipe = pipeline.register(new TemplateToNeuralPipe());

            PairMerge<NeuralModel, Stream<NeuralSample>> pairMerge = pipeline.register(new PairMerge<>());

            neuralLearningBuilder = new NeuralLearningBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> trainingPipeline = pipeline.registerEnd(neuralLearningBuilder.buildPipeline());

            extractionBranch.connectAfterL(groundingPipeline);
            extractionBranch.connectAfterR(templateToNeuralPipe);

            pairMerge.connectBeforeL(templateToNeuralPipe);
            pairMerge.connectBeforeR(groundingPipeline);

            pairMerge.connectAfter(trainingPipeline);

            return pipeline;
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