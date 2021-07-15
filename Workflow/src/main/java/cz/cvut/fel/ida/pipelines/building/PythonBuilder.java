package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.pipelines.pipes.generic.LambdaPipe;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.export.NeuralSerializer;
import cz.cvut.fel.ida.pipelines.pipes.specific.NeuralSerializerPipe;
import cz.cvut.fel.ida.pipelines.pipes.specific.TemplateToNeuralPipe;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.pipes.generic.DuplicateBranch;
import cz.cvut.fel.ida.pipelines.pipes.generic.FirstFromPairExtractionBranch;
import cz.cvut.fel.ida.pipelines.pipes.generic.PairMerge;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


public class PythonBuilder extends AbstractPipelineBuilder<Sources, Pair<NeuralModel, Stream<NeuralSample>>> {
    private final WeightFactory weightFactory;

    private DuplicateBranch<Template> templateDuplicateBranch;

    public PythonBuilder(Settings settings) {
        super(settings);
        this.weightFactory = new WeightFactory(new AtomicInteger(0));
    }

    public Pipe<Template, NeuralModel> convertModel() {
        return new TemplateToNeuralPipe(settings);
    }

    public Pipeline<Sources, Pair<Template, Stream<LogicSample>>> buildFromSources(Template template, Sources sources, Settings settings) {
        TemplateSamplesBuilder templateSamplesBuilder = new TemplateSamplesBuilder(sources, settings, template);
        return templateSamplesBuilder.buildPipeline();
    }

    public Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> buildGrounding(Settings settings, WeightFactory weightFactory) {
        GroundingBuilder groundingBuilder = new GroundingBuilder(settings, weightFactory);
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = groundingBuilder.buildPipeline();
        return groundingPipeline;
    }

    public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildNeuralNets(Settings settings, WeightFactory weightFactory) {
        NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, weightFactory);
        return neuralNetsBuilder.buildPipeline();
    }

    public Template buildTemplate(Sources sources) throws Exception {
        TemplateSamplesBuilder templateSamplesBuilder = new TemplateSamplesBuilder(sources, settings);
        return templateSamplesBuilder.getSourcesTemplatePipeline(sources, settings).execute(sources).s;
    }

    public Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> buildPipeline(Template template, Stream<LogicSample> logicSamples) {
        Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> pipeline = new Pipeline<>("PythonNNbuilding", this);

        //simple pipes
        FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.register(new FirstFromPairExtractionBranch<>());
        templateDuplicateBranch = pipeline.register(new DuplicateBranch<>());

        //pipelines
        LambdaPipe<Sources, Pair<Template, Stream<LogicSample>>> templateIdentityPipe = pipeline.registerStart(
                new LambdaPipe<>("TemplateIdentityPipe", s -> new Pair<>(template, logicSamples), settings)
        );

        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(buildGrounding(settings, weightFactory));
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.register(buildNeuralNets(settings, weightFactory));

        //connecting the execution graph
        templateIdentityPipe.connectAfter(templateSamplesBranch);
        templateSamplesBranch.connectAfterL(groundingPipeline).connectAfter(neuralizationPipeline);
        templateSamplesBranch.connectAfterR(templateDuplicateBranch);

        PairMerge<NeuralModel, Stream<NeuralSample>> neuralMerge = pipeline.registerEnd(new PairMerge<>());

        //prepare for training
        Pipe<Template, NeuralModel> template2NeuralModelPipe = pipeline.register(convertModel());
        templateDuplicateBranch.connectAfterL(template2NeuralModelPipe);

        neuralMerge.connectBeforeL(template2NeuralModelPipe);
        neuralMerge.connectBeforeR(neuralizationPipeline);

        return pipeline;
    }

    public Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> buildPipeline(Template template, Sources sources) {
        Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> pipeline = new Pipeline<>("PythonNNbuilding", this);

        //simple pipes
        FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.register(new FirstFromPairExtractionBranch<>());
        templateDuplicateBranch = pipeline.register(new DuplicateBranch<>());

        //pipelines
        Pipeline<Sources, Pair<Template, Stream<LogicSample>>> sourcesPairPipeline = pipeline.registerStart(buildFromSources(template, sources, settings));
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(buildGrounding(settings, weightFactory));
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.register(buildNeuralNets(settings, weightFactory));

        //connecting the execution graph
        sourcesPairPipeline.connectAfter(templateSamplesBranch);
        templateSamplesBranch.connectAfterL(groundingPipeline).connectAfter(neuralizationPipeline);
        templateSamplesBranch.connectAfterR(templateDuplicateBranch);

        PairMerge<NeuralModel, Stream<NeuralSample>> neuralMerge = pipeline.registerEnd(new PairMerge<>());

        //prepare for training
        Pipe<Template, NeuralModel> template2NeuralModelPipe = pipeline.register(convertModel());
        templateDuplicateBranch.connectAfterL(template2NeuralModelPipe);

        neuralMerge.connectBeforeL(template2NeuralModelPipe);
        neuralMerge.connectBeforeR(neuralizationPipeline);

        return pipeline;
    }

    @Override
    public Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> buildPipeline() {
        return null;
    }

    public Pair<List<NeuralSerializer.SerializedWeight>, Stream<NeuralSerializer.SerializedSample>> getSerializedNNs() throws Exception {
        Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> buildNNsPipeline = buildPipeline();
        NeuralSerializerPipe neuralSerializerPipe = new NeuralSerializerPipe();
        Pipe<Pair<NeuralModel, Stream<NeuralSample>>, Pair<List<NeuralSerializer.SerializedWeight>, Stream<NeuralSerializer.SerializedSample>>> serializationPipe = buildNNsPipeline.connectAfter(neuralSerializerPipe);
        return serializationPipe.get();
    }
}
