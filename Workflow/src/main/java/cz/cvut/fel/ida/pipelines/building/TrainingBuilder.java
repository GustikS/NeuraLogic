package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.pipelines.Merge;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.RecurrentPipe;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.pipes.generic.DuplicateBranch;
import cz.cvut.fel.ida.pipelines.pipes.generic.FirstFromPairExtractionBranch;
import cz.cvut.fel.ida.pipelines.pipes.generic.LambdaPipe;
import cz.cvut.fel.ida.pipelines.pipes.generic.PairMerge;
import cz.cvut.fel.ida.pipelines.pipes.specific.NeuralTrainingPipe;
import cz.cvut.fel.ida.pipelines.pipes.specific.TemplateToNeuralPipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Source;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.exporting.Exporter;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingBuilder extends AbstractPipelineBuilder<Sources, Pair<Pair<Template, NeuralModel>, Progress>> {
    private static final Logger LOG = Logger.getLogger(TrainingBuilder.class.getName());
    Sources sources;

    private static int exportNumber = 0;

    public TrainingBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> buildPipeline() {
        return buildPipeline(sources);
    }

    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> buildPipeline(Sources sources) {
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> pipeline = new Pipeline<>("TrainingPipeline", this);

        if (sources.templateProvided) {
            LogicLearningBuilder logicTrainingBuilder = new LogicLearningBuilder(settings);
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Progress>> trainingPipeline = pipeline.registerEnd(logicTrainingBuilder.buildPipeline());

            TemplateSamplesBuilder templateSamplesBuilder = new TemplateSamplesBuilder(sources, settings);
            Pipeline<Sources, Pair<Template, Stream<LogicSample>>> templateSourcesPipeline = pipeline.registerStart(templateSamplesBuilder.buildPipeline());
            templateSourcesPipeline.connectAfter(trainingPipeline);

        } else { //Structure Learning
            Pipe<Sources, Source> getTrainSource = pipeline.register(new LambdaPipe<Sources, Source>("getTrainSourcePipe", srcs -> srcs.train, settings));
            SamplesProcessingBuilder trainingSamplesProcessor = new SamplesProcessingBuilder(settings, sources.train);
            Pipeline<Source, Stream<LogicSample>> getLogicSampleStream = pipeline.register(trainingSamplesProcessor.buildPipeline());
            getTrainSource.connectAfter(getLogicSampleStream);
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
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Progress>> pipeline = new Pipeline<>("LearningPipeline", this);

            FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.registerStart(new FirstFromPairExtractionBranch<>());
            DuplicateBranch<Template> duplicateBranch = pipeline.register(new DuplicateBranch<>());

            GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
            Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(groundingBuilder.buildPipeline());

            NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, groundingBuilder.weightFactory);
            Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.register(neuralNetsBuilder.buildPipeline());

            TemplateToNeuralPipe templateToNeuralPipe = pipeline.register(new TemplateToNeuralPipe(settings));

            PairMerge<NeuralModel, Stream<NeuralSample>> neuralMerge = pipeline.register(new PairMerge<>("NeuralMerge"));

            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> trainingPipeline = pipeline.register(new NeuralLearningBuilder(settings).buildPipeline());

            Merge<Template, Pair<NeuralModel, Progress>, Pair<Pair<Template, NeuralModel>, Progress>> finalMerge = pipeline.registerEnd(new Merge<Template, Pair<NeuralModel, Progress>, Pair<Pair<Template, NeuralModel>, Progress>>("ModelMerge", settings) {
                @Override
                protected Pair<Pair<Template, NeuralModel>, Progress> merge(Template template, Pair<NeuralModel, Progress> training) {
                    if (settings.exportTrainedModel) {   //the weights are the same objects, so no need to transfer their trained values
                        Exporter exporter = Exporter.getExporter(settings.exportDir, "/models/trainedTemplate" + exportNumber++, "JAVA");
                        exporter.export(template);
                    }
                    return new Pair<>(new Pair<>(template, training.r), training.s);
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

    public static class StructureLearningBuilder extends AbstractPipelineBuilder<Stream<LogicSample>, Pair<Pair<Template, NeuralModel>, Progress>> {

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

    public static class NeuralLearningBuilder extends AbstractPipelineBuilder<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> {

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