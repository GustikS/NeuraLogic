package pipelines.building;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import networks.computation.results.Results;
import pipelines.Merge;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.RecurrentPipe;
import pipelines.pipes.generic.DuplicateBranch;
import pipelines.pipes.generic.FirstFromPairExtractionBranch;
import pipelines.pipes.generic.PairMerge;
import pipelines.pipes.specific.TemplateToNeuralPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingBuilder extends AbstractPipelineBuilder<Sources, Pair<Pair<Template, NeuralModel>,Results>> {
    private static final Logger LOG = Logger.getLogger(TrainingBuilder.class.getName());
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

        Pipe<Sources, Source> getTrainSource = pipeline.register(new Pipe<Sources, Source>("getTrainSourcePipe") {
            @Override
            public Source apply(Sources sources) {
                return sources.train;
            }
        });

        SamplesProcessingBuilder trainingSamplesProcessor = new SamplesProcessingBuilder(settings, sources.train);
        Pipeline<Source, Stream<LogicSample>> getLogicSampleStream = pipeline.register(trainingSamplesProcessor.buildPipeline());
        getTrainSource.connectAfter(getLogicSampleStream);

        if (sources.templateProvided) {
            DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>());
            duplicateBranch.connectAfterL(getTrainSource);

            TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);
            Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());

            PairMerge<Template, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());
            LogicLearningBuilder logicTrainingBuilder = new LogicLearningBuilder(settings);
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Results>> trainingPipeline = pipeline.registerEnd(logicTrainingBuilder.buildPipeline());

            sourcesTemplatePipeline.connectAfter(pairMerge.input1);
            duplicateBranch.connectAfterR(sourcesTemplatePipeline);
            pairMerge.connectBeforeR(getLogicSampleStream);
            pairMerge.connectAfter(trainingPipeline);

        } else { //Structure Learning
            pipeline.registerStart(getTrainSource);
            Pipeline<Stream<LogicSample>, Pair<Pair<Template, NeuralModel>, Results>> trainingPipeline = pipeline.registerEnd(new StructureLearningBuilder(settings).buildPipeline());
            getLogicSampleStream.connectAfter(trainingPipeline);
        }

        return pipeline;
    }

    /**
     * TODO - in case that some of the samples did not ground succesfully, hack the pipeline and start StructureLearning in the current context
     */
    public class LogicLearningBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Results>> {

        public LogicLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Results>> buildPipeline() {
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Results>> pipeline = new Pipeline<>("LogicLearningPipeline");

            FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.registerStart(new FirstFromPairExtractionBranch<>());
            DuplicateBranch<Template> duplicateBranch = pipeline.register(new DuplicateBranch<>());

            Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> groundingPipeline = pipeline.register(new GroundingBuilder(settings).buildPipeline());

            TemplateToNeuralPipe templateToNeuralPipe = pipeline.register(new TemplateToNeuralPipe());

            PairMerge<NeuralModel, Stream<NeuralSample>> pairMerge = pipeline.register(new PairMerge<>());

            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> trainingPipeline = pipeline.register(new NeuralLearningBuilder(settings).buildPipeline());

            Merge<Template, Pair<NeuralModel, Results>, Pair<Pair<Template, NeuralModel>, Results>> finalMerge = pipeline.registerEnd(new Merge<Template, Pair<NeuralModel, Results>, Pair<Pair<Template, NeuralModel>, Results>>("ModelMerge") {
                @Override
                protected Pair<Pair<Template, NeuralModel>, Results> merge(Template input1, Pair<NeuralModel, Results> input2) {
                    return new Pair<>(new Pair<>(input1, input2.r), input2.s);
                }
            });

            templateSamplesBranch.connectAfterL(groundingPipeline);
            templateSamplesBranch.connectAfterR(duplicateBranch);

            duplicateBranch.connectAfterL(templateToNeuralPipe);

            pairMerge.connectBeforeL(templateToNeuralPipe);
            pairMerge.connectBeforeR(groundingPipeline);

            pairMerge.connectAfter(trainingPipeline);

            finalMerge.connectBeforeL(duplicateBranch.output2);
            finalMerge.connectBeforeR(trainingPipeline);

            return pipeline;
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