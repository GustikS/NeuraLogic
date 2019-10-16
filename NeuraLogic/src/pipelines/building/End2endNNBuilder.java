package pipelines.building;

import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundingSample;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.building.Neuralizer;
import pipelines.Merge;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.pipes.generic.DuplicateBranch;
import pipelines.pipes.generic.FirstFromPairExtractionBranch;
import pipelines.pipes.generic.PairMerge;
import pipelines.pipes.specific.TemplateToNeuralPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class End2endNNBuilder extends AbstractPipelineBuilder<Sources, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>> {
    private static final Logger LOG = Logger.getLogger(End2endNNBuilder.class.getName());


    private final Sources sources;

    public End2endNNBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>> buildPipeline() {
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>> pipeline = new Pipeline<>("End2endNNBuilder", this);

        Pipe<Sources, Source> getSource = null;
        SamplesProcessingBuilder samplesProcessor = null;
        if (sources.trainOnly) {
            getSource = pipeline.register(new Pipe<Sources, Source>("getTrainSourcePipe") {
                @Override
                public Source apply(Sources sources) {
                    return sources.train;
                }
            });
            samplesProcessor = new SamplesProcessingBuilder(settings, sources.train);

        } else if (sources.testOnly) {
            getSource = pipeline.register(new Pipe<Sources, Source>("getTestSourcePipe") {
                @Override
                public Source apply(Sources sources) {
                    return sources.test;
                }
            });
            samplesProcessor = new SamplesProcessingBuilder(settings, sources.test);
        } else {
            LOG.severe("Can only ground a single source (train/test) at a time");
            throw new UnsupportedOperationException();
        }

        Pipeline<Source, Stream<LogicSample>> getLogicSampleStream = pipeline.register(samplesProcessor.buildPipeline());
        getSource.connectAfter(getLogicSampleStream);

        if (!sources.templateProvided) {
            LOG.severe("The template must be provided in the simplified buildFromSource mode.");
            throw new UnsupportedOperationException();
        }

        DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>("TemplateSamplesBranch"));
        duplicateBranch.connectAfterL(getSource);

        TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);
        Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());

        PairMerge<Template, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>("TemplateSamplesMerge"));

        Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>> neuralPipeline = pipeline.registerEnd(groundAndNeuralizePipeline());

        duplicateBranch.connectAfterR(sourcesTemplatePipeline);
        pairMerge.connectBeforeL(sourcesTemplatePipeline);
        pairMerge.connectBeforeR(getLogicSampleStream);
        pairMerge.connectAfter(neuralPipeline);

        settings.root = pipeline;
        return pipeline;
    }


    public Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>> groundAndNeuralizePipeline() {
        Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>> pipeline = new Pipeline<>("GroundAndNeuralizePipeline", settings);

        FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.registerStart(new FirstFromPairExtractionBranch<>());
        DuplicateBranch<Template> duplicateBranch = pipeline.register(new DuplicateBranch<>());

        GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(groundingBuilder.buildPipeline());

        NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, new Neuralizer(groundingBuilder.grounder));
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.register(neuralNetsBuilder.buildPipeline());

        TemplateToNeuralPipe templateToNeuralPipe = pipeline.register(new TemplateToNeuralPipe());

        PairMerge<NeuralModel, Stream<NeuralSample>> neuralMerge = pipeline.register(new PairMerge<>("NeuralMerge"));

        Merge<Template, Pair<NeuralModel, Stream<NeuralSample>>, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>> finalMerge = pipeline.registerEnd(
                new Merge<Template, Pair<NeuralModel, Stream<NeuralSample>>, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>>("ModelMerge") {
                    @Override
                    protected Pair<Pair<Template, NeuralModel>, List<NeuralSample>> merge(Template input1, Pair<NeuralModel, Stream<NeuralSample>> input2) {
                        return new Pair<>(new Pair<>(input1, input2.r), input2.s.collect(Collectors.toList()));
                    }
                });

        templateSamplesBranch.connectAfterL(groundingPipeline);
        templateSamplesBranch.connectAfterR(duplicateBranch);

        groundingPipeline.connectAfter(neuralizationPipeline);

        duplicateBranch.connectAfterL(templateToNeuralPipe);

        neuralMerge.connectBeforeL(templateToNeuralPipe);
        neuralMerge.connectBeforeR(neuralizationPipeline);

        finalMerge.connectBeforeL(duplicateBranch.output2);
        finalMerge.connectBeforeR(neuralMerge);

        return pipeline;
    }
}
