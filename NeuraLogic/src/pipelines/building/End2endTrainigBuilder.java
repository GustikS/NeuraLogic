package pipelines.building;

import constructs.building.factories.WeightFactory;
import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundingSample;
import networks.computation.evaluation.results.Progress;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.building.Neuralizer;
import networks.structure.export.NeuralSerializer;
import pipelines.Merge;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.pipes.generic.DuplicateBranch;
import pipelines.pipes.generic.FirstFromPairExtractionBranch;
import pipelines.pipes.generic.PairMerge;
import pipelines.pipes.specific.NeuralSerializerPipe;
import pipelines.pipes.specific.TemplateToNeuralPipe;
import pipelines.pipes.specific.TrainingResultTemplateMerge;
import settings.Settings;
import settings.Sources;
import utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class End2endTrainigBuilder extends AbstractPipelineBuilder<Sources, Pair<Pair<Template, NeuralModel>, Progress>> {
    private static final Logger LOG = Logger.getLogger(End2endTrainigBuilder.class.getName());

    private final Sources sources;

    public End2endTrainigBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> buildPipeline() {
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> pipeline = new Pipeline<>("End2endNNBuilder", this);

        //to transfer parameters from groundings to neural nets
        WeightFactory weightFactory = new WeightFactory();

        //pipelines
        Pipeline<Sources, Pair<Template, Stream<LogicSample>>> sourcesPairPipeline = pipeline.registerStart(buildFromSources(sources, settings));
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(buildGrounding(settings, weightFactory));
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.register(buildNeuralNets(settings, weightFactory));
        Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> trainingPipeline = pipeline.register(buildTraining(settings));

        //simple pipes
        Pipe<Template, NeuralModel> templateNeuralModelPipe = pipeline.register(convertModel());

        //helper blocks
        FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.register(new FirstFromPairExtractionBranch<>());
        DuplicateBranch<Template> templateDuplicateBranch = pipeline.register(new DuplicateBranch<>());
        PairMerge<NeuralModel, Stream<NeuralSample>> neuralMerge = pipeline.register(new PairMerge<>());
        Merge<Template, Pair<NeuralModel, Progress>, Pair<Pair<Template, NeuralModel>, Progress>> trainingResultTemplateMerge = pipeline.registerEnd(trainingResultsTemplateMerge());

        //connecting the execution graph
        sourcesPairPipeline.connectAfter(templateSamplesBranch);
        templateSamplesBranch.connectAfterL(groundingPipeline).connectAfter(neuralizationPipeline);
        templateSamplesBranch.connectAfterR(templateDuplicateBranch);
        templateDuplicateBranch.connectAfterL(templateNeuralModelPipe);

        neuralMerge.connectBeforeL(templateNeuralModelPipe);
        neuralMerge.connectBeforeR(neuralizationPipeline);
        neuralMerge.connectAfter(trainingPipeline);

        trainingResultTemplateMerge.connectBeforeL(templateDuplicateBranch.output2);
        trainingResultTemplateMerge.connectBeforeR(trainingPipeline);

        settings.root = pipeline;
        return pipeline;
    }

    public Pipeline<Sources, Pair<Template, Stream<LogicSample>>> buildFromSources(Sources sources, Settings settings) {
        TemplateSamplesBuilder templateSamplesBuilder = new TemplateSamplesBuilder(sources,settings);
        return templateSamplesBuilder.buildPipeline();
    }

    public Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> buildGrounding(Settings settings, WeightFactory weightFactory) {
        GroundingBuilder groundingBuilder = new GroundingBuilder(settings, weightFactory);
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = groundingBuilder.buildPipeline();
        return groundingPipeline;
    }

    public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildNeuralNets(Settings settings, WeightFactory weightFactory) {
        NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, new Neuralizer(settings, weightFactory));
        return neuralNetsBuilder.buildPipeline();
    }

    public Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> buildTraining(Settings settings) {
        return new TrainingBuilder(settings, null).new NeuralLearningBuilder(settings).buildPipeline();
    }

    public Pipe<Template, NeuralModel> convertModel() {
        return new TemplateToNeuralPipe(settings);
    }

    public TrainingResultTemplateMerge trainingResultsTemplateMerge() {
        return new TrainingResultTemplateMerge();
    }

    Pipe<Pair<NeuralModel, Stream<NeuralSample>>, Pair<List<NeuralSerializer.SerializedWeight>, Stream<NeuralSerializer.SerializedSample>>> getSerializationPipe(){
        return new NeuralSerializerPipe();
    }
}
