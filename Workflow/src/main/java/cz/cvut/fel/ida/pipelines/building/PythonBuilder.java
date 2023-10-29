package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.pipes.generic.*;
import cz.cvut.fel.ida.pipelines.pipes.specific.TemplateToNeuralPipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.stream.Stream;


public class PythonBuilder extends AbstractPipelineBuilder<Sources, Pair<Stream<GroundingSample>, Stream<NeuralSample>>> {
    private final WeightFactory weightFactory;

    private DuplicateBranch<Template> templateDuplicateBranch;

    public PythonBuilder(Settings settings) {
        super(settings);
        this.weightFactory = new WeightFactory(settings.inferred.maxWeightCount);
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

    public Pipeline<Sources, Pair<Stream<GroundingSample>, Stream<NeuralSample>>> buildPipeline(
            Template template,
            Stream<LogicSample> logicSamples,
            IntConsumer progressCallback
    ) {
        Pipeline<Sources, Pair<Stream<GroundingSample>, Stream<NeuralSample>>> pipeline = new Pipeline<>("PythonNNbuilding", this);

        //simple pipes
        FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.register(new FirstFromPairExtractionBranch<>());

        //pipelines
        LambdaPipe<Sources, Pair<Template, Stream<LogicSample>>> templateIdentityPipe = pipeline.registerStart(
                new LambdaPipe<>("TemplateIdentityPipe", s -> new Pair<>(template, logicSamples), settings)
        );

        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(buildGrounding(settings, weightFactory));
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.register(buildNeuralNets(settings, weightFactory));

        IdentityGenPipe<Stream<GroundingSample>> helperPipe = pipeline.register(new IdentityGenPipe<>());
        StreamTeeBranch<GroundingSample, Stream<GroundingSample>> groundingBranch = pipeline.register(new StreamTeeBranch<>());

        groundingBranch.connectAfterL(neuralizationPipeline);
        groundingBranch.connectAfterR(helperPipe);

        //connecting the execution graph
        templateIdentityPipe.connectAfter(templateSamplesBranch);
        templateSamplesBranch.connectAfterL(groundingPipeline).connectAfter(groundingBranch);

        PairMerge<Stream<GroundingSample>, Stream<NeuralSample>> neuralMerge = attachProgressCallback(progressCallback, pipeline);

        neuralMerge.connectBeforeL(helperPipe);
        neuralMerge.connectBeforeR(neuralizationPipeline);

        return pipeline;
    }

    public Pipeline<Sources, Pair<Stream<GroundingSample>, Stream<NeuralSample>>> buildPipeline(
            Template template,
            Sources sources,
            IntConsumer progressCallback
    ) {
        Pipeline<Sources, Pair<Stream<GroundingSample>, Stream<NeuralSample>>> pipeline = new Pipeline<>("PythonNNbuilding", this);

        //simple pipes
        FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.register(new FirstFromPairExtractionBranch<>());

        //pipelines
        Pipeline<Sources, Pair<Template, Stream<LogicSample>>> sourcesPairPipeline = pipeline.registerStart(buildFromSources(template, sources, settings));
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(buildGrounding(settings, weightFactory));
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.register(buildNeuralNets(settings, weightFactory));

        IdentityGenPipe<Stream<GroundingSample>> helperPipe = pipeline.register(new IdentityGenPipe<>());
        StreamTeeBranch<GroundingSample, Stream<GroundingSample>> groundingBranch = pipeline.register(new StreamTeeBranch<>());

        groundingBranch.connectAfterL(neuralizationPipeline);
        groundingBranch.connectAfterR(helperPipe);

        //connecting the execution graph
        sourcesPairPipeline.connectAfter(templateSamplesBranch);
        templateSamplesBranch.connectAfterL(groundingPipeline).connectAfter(groundingBranch);

        PairMerge<Stream<GroundingSample>, Stream<NeuralSample>> neuralMerge = attachProgressCallback(progressCallback, pipeline);

        neuralMerge.connectBeforeL(helperPipe);
        neuralMerge.connectBeforeR(neuralizationPipeline);

        return pipeline;
    }

    private PairMerge<Stream<GroundingSample>, Stream<NeuralSample>> attachProgressCallback(
            IntConsumer callback,
            Pipeline<Sources, Pair<Stream<GroundingSample>, Stream<NeuralSample>>> pipeline
    ) {
        PairMerge<Stream<GroundingSample>, Stream<NeuralSample>> neuralMerge;

        if (callback == null) {
            return pipeline.registerEnd(new PairMerge<>());
        }

        AtomicInteger counter = new AtomicInteger();
        neuralMerge = pipeline.register(new PairMerge<>());

        LambdaPipe<Pair<Stream<GroundingSample>, Stream<NeuralSample>>, Pair<Stream<GroundingSample>, Stream<NeuralSample>>> progressCallbackPipe = pipeline.registerEnd(
                new LambdaPipe<>("ProgressCallbackPipe", pair -> {
                    Stream<NeuralSample> stream = pair.s.peek(sample -> callback.accept(counter.incrementAndGet()));

                    return new Pair<>(pair.r, stream);
                }, settings)
        );

        neuralMerge.connectAfter(progressCallbackPipe);
        return neuralMerge;
    }

    @Override
    public Pipeline<Sources, Pair<Stream<GroundingSample>, Stream<NeuralSample>>> buildPipeline() {
        return null;
    }
}
