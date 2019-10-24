package pipelines.building;

import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundingSample;
import networks.computation.evaluation.results.Progress;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.building.Neuralizer;
import pipelines.Merge;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.RecurrentPipe;
import pipelines.pipes.generic.DuplicateBranch;
import pipelines.pipes.generic.FirstFromPairExtractionBranch;
import pipelines.pipes.generic.PairMerge;
import pipelines.pipes.specific.NeuralTrainingPipe;
import pipelines.pipes.specific.TemplateToNeuralPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingBuilder extends AbstractPipelineBuilder<Sources, Pair<Pair<Template, NeuralModel>,Progress>> {
    private static final Logger LOG = Logger.getLogger(TrainingBuilder.class.getName());
    Sources sources;

    public TrainingBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> buildPipeline(){
        return buildPipeline(sources);
    }


    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> buildPipeline(Sources sources) {
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> pipeline = new Pipeline<>("TrainingPipeline", this);

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
            DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>("TemplateSamplesBranch"));
            duplicateBranch.connectAfterL(getTrainSource);

            TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);
            Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());

            PairMerge<Template, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>("TemplateSamplesMerge"));
            LogicLearningBuilder logicTrainingBuilder = new LogicLearningBuilder(settings);
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Progress>> trainingPipeline = pipeline.registerEnd(logicTrainingBuilder.buildPipeline());

            duplicateBranch.connectAfterR(sourcesTemplatePipeline);
            pairMerge.connectBeforeL(sourcesTemplatePipeline);
            pairMerge.connectBeforeR(getLogicSampleStream);
            pairMerge.connectAfter(trainingPipeline);

        } else { //Structure Learning
            pipeline.registerStart(getTrainSource);
            Pipeline<Stream<LogicSample>, Pair<Pair<Template, NeuralModel>, Progress>> trainingPipeline = pipeline.registerEnd(new StructureLearningBuilder(settings).buildPipeline());
            getLogicSampleStream.connectAfter(trainingPipeline);
        }

        return pipeline;
    }

    /**
     * todo - in case that some of the samples did not ground successfully, hack the pipeline and start StructureLearning in the current context
     */
    public class LogicLearningBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Progress>> {

        public LogicLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Progress>> buildPipeline() {
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Progress>> pipeline = new Pipeline<>("LogicLearningPipeline", this);

            FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.registerStart(new FirstFromPairExtractionBranch<>());
            DuplicateBranch<Template> duplicateBranch = pipeline.register(new DuplicateBranch<>());

            GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
            Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(groundingBuilder.buildPipeline());


            NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, new Neuralizer(settings, groundingBuilder.grounder.weightFactory));
            Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.register(neuralNetsBuilder.buildPipeline());

            TemplateToNeuralPipe templateToNeuralPipe = pipeline.register(new TemplateToNeuralPipe(settings));

            PairMerge<NeuralModel, Stream<NeuralSample>> neuralMerge = pipeline.register(new PairMerge<>("NeuralMerge"));

            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> trainingPipeline = pipeline.register(new NeuralLearningBuilder(settings).buildPipeline());

            Merge<Template, Pair<NeuralModel, Progress>, Pair<Pair<Template, NeuralModel>, Progress>> finalMerge = pipeline.registerEnd(new Merge<Template, Pair<NeuralModel, Progress>, Pair<Pair<Template, NeuralModel>, Progress>>("ModelMerge") {
                @Override
                protected Pair<Pair<Template, NeuralModel>, Progress> merge(Template input1, Pair<NeuralModel, Progress> input2) {
                    return new Pair<>(new Pair<>(input1, input2.r), input2.s);
                }
            });

            templateSamplesBranch.connectAfterL(groundingPipeline);
            templateSamplesBranch.connectAfterR(duplicateBranch);

            groundingPipeline.connectAfter(neuralizationPipeline);

            duplicateBranch.connectAfterL(templateToNeuralPipe);

            neuralMerge.connectBeforeL(templateToNeuralPipe);
            neuralMerge.connectBeforeR(neuralizationPipeline);

            neuralMerge.connectAfter(trainingPipeline);

            finalMerge.connectBeforeL(duplicateBranch.output2);
            finalMerge.connectBeforeR(trainingPipeline);

            return pipeline;
        }
    }

    public class StructureLearningBuilder extends AbstractPipelineBuilder<Stream<LogicSample>, Pair<Pair<Template, NeuralModel>, Progress>> {

        public StructureLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Stream<LogicSample>, Pair<Pair<Template, NeuralModel>, Progress>> buildPipeline() {
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

    public class NeuralLearningBuilder extends AbstractPipelineBuilder<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>>{

        public NeuralLearningBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> buildPipeline() {
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> pipeline = new Pipeline<>("NeuralTrainingPipeline", this);
            NeuralTrainingPipe neuralTrainingPipe = pipeline.registerEnd(pipeline.registerStart(new NeuralTrainingPipe(settings)));
            return pipeline;
        }
    }

}