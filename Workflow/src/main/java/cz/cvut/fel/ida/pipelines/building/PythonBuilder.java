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


public class PythonBuilder {
    private final WeightFactory weightFactory;

    private final Settings settings;

    private final PythonGroundingPipeline groundingPipeline;

    private final PythonNeuralizationPipeline neuralizationPipeline;

    public PythonBuilder(Settings settings) {
        this.settings = settings;
        this.weightFactory = new WeightFactory(settings.inferred.maxWeightCount);

        this.groundingPipeline = new PythonGroundingPipeline(settings);
        this.neuralizationPipeline = new PythonNeuralizationPipeline(settings);
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
        return groundingBuilder.buildPipeline();
    }

    public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildNeuralNets(Settings settings, WeightFactory weightFactory) {
        NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, weightFactory);
        return neuralNetsBuilder.buildPipeline();
    }

    public Template buildTemplate(Sources sources) throws Exception {
        TemplateSamplesBuilder templateSamplesBuilder = new TemplateSamplesBuilder(sources, settings);
        return templateSamplesBuilder.getSourcesTemplatePipeline(sources, settings).execute(sources).s;
    }

    class PythonGroundingPipeline extends AbstractPipelineBuilder<Sources, Stream<GroundingSample>> {
        public PythonGroundingPipeline(Settings settings) {
            super(settings);
        }

        public Pipeline<Sources, Stream<GroundingSample>> buildPipeline() {
            return null;
        }

        public Pipeline<Sources, Stream<GroundingSample>> buildPipeline(
                Template template,
                Stream<LogicSample> logicSamples
        ) {
            Pipeline<Sources, Stream<GroundingSample>> pipeline = new Pipeline<>("PythonGroundingBuilding", this);

            //pipelines
            LambdaPipe<Sources, Pair<Template, Stream<LogicSample>>> templateIdentityPipe = pipeline.registerStart(
                    new LambdaPipe<>("TemplateIdentityPipe", s -> new Pair<>(template, logicSamples), settings)
            );

            Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.registerEnd(buildGrounding(settings, weightFactory));

            //connecting the execution graph
            templateIdentityPipe.connectAfter(groundingPipeline);

            return pipeline;
        }

        public Pipeline<Sources, Stream<GroundingSample>> buildPipeline(
                Template template,
                Sources sources
        ) {
            Pipeline<Sources, Stream<GroundingSample>> pipeline = new Pipeline<>("PythonGroundingBuilding", this);

            //pipelines
            Pipeline<Sources, Pair<Template, Stream<LogicSample>>> sourcesPairPipeline = pipeline.registerStart(buildFromSources(template, sources, settings));
            Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.registerEnd(buildGrounding(settings, weightFactory));

            //connecting the execution graph
            sourcesPairPipeline.connectAfter(groundingPipeline);

            return pipeline;
        }
    }

    class PythonNeuralizationPipeline extends AbstractPipelineBuilder<Stream<GroundingSample>, Stream<NeuralSample>> {
        public PythonNeuralizationPipeline(Settings settings) {
            super(settings);
        }

        public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildPipeline() {
            return null;
        }

        public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildPipeline(
                Stream<GroundingSample> groundings,
                IntConsumer callback
        ) {
            Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> pipeline = new Pipeline<>("PythonGroundingbBilding", this);

            //pipelines
            LambdaPipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingIdentityPipe = pipeline.registerStart(
                    new LambdaPipe<>("NeuralizationIdentityPipe", s -> groundings, settings)
            );

            Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline;

            if (callback != null) {
                neuralizationPipeline = pipeline.register(buildNeuralNets(settings, weightFactory));
                AtomicInteger counter = new AtomicInteger();

                LambdaPipe<Stream<NeuralSample>, Stream<NeuralSample>> progressCallbackPipe = pipeline.registerEnd(
                        new LambdaPipe<>("ProgressCallbackPipe", samples -> samples.peek(sample -> callback.accept(counter.incrementAndGet())), settings)
                );

                neuralizationPipeline.connectAfter(progressCallbackPipe);
            } else {
                neuralizationPipeline = pipeline.registerEnd(buildNeuralNets(settings, weightFactory));
            }

            groundingIdentityPipe.connectAfter(neuralizationPipeline);
            return pipeline;
        }
    }


    public Pipeline<Sources, Stream<GroundingSample>> buildGroundings(
            Template template,
            Stream<LogicSample> logicSamples
    ) {
        return this.groundingPipeline.buildPipeline(template, logicSamples);
    }

    public Pipeline<Sources, Stream<GroundingSample>> buildGroundings(
            Template template,
            Sources sources
    ) {
        return this.groundingPipeline.buildPipeline(template, sources);
    }

    public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralize(
            Stream<GroundingSample> groundings,
            IntConsumer progressCallback
    ) {
        return this.neuralizationPipeline.buildPipeline(groundings, progressCallback);
    }
}
