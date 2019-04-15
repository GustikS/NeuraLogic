package pipelines.building;

import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundingSample;
import networks.computation.evaluation.results.Results;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.building.Neuralizer;
import pipelines.Branch;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.pipes.generic.DuplicateBranch;
import pipelines.pipes.generic.PairMerge;
import pipelines.pipes.specific.NeuralEvaluationPipe;
import pipelines.pipes.specific.TemplateToNeuralPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TestingBuilder extends AbstractPipelineBuilder<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(TestingBuilder.class.getName());
    Sources sources;

    public TestingBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, Results> buildPipeline() {
        return buildPipeline(this.sources);
    }


    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        Pipeline<Sources, Results> pipeline = new Pipeline<>("TestingPipeline", this);
        DuplicateBranch<Sources> duplicateSources = pipeline.registerStart(new DuplicateBranch<>());
        Pipe<Sources, Source> getTestSourcePipe = pipeline.register(new Pipe<Sources, Source>("getTestSourcePipe") {
            @Override
            public Source apply(Sources sources) {
                return sources.test;
            }
        });

        Pipeline<Source, Stream<LogicSample>> testSampleStream = pipeline.register(new SamplesProcessingBuilder(settings, sources.test).buildPipeline());
        Pipeline<Sources, Template> templatePipeline = pipeline.register(new TemplateProcessingBuilder(settings, sources).buildPipeline());

        DuplicateBranch<Template> duplicateTemplate = pipeline.register(new DuplicateBranch<>());
        TemplateToNeuralPipe templateToNeuralPipe = pipeline.register(new TemplateToNeuralPipe());
        PairMerge<Pair<Template, NeuralModel>, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());
        TestingBuilder.LogicTestingBuilder testingBuilder = (new TestingBuilder(settings, sources)).new LogicTestingBuilder(settings);
        Pipeline<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> logicTestingPipeline = pipeline.registerEnd(testingBuilder.buildPipeline());
        PairMerge<Template, NeuralModel> modelMerge = pipeline.register(new PairMerge<>());

        duplicateSources.connectAfterL(getTestSourcePipe);
        duplicateSources.connectAfterR(templatePipeline);

        getTestSourcePipe.connectAfter(testSampleStream);
        templatePipeline.connectAfter(duplicateTemplate);
        duplicateTemplate.connectAfterL(templateToNeuralPipe);

        modelMerge.connectBeforeL(duplicateTemplate.output2);
        modelMerge.connectBeforeR(templateToNeuralPipe);

        pairMerge.connectBeforeL(modelMerge);
        pairMerge.connectBeforeR(testSampleStream);
        pairMerge.connectAfter(logicTestingPipeline);

        return pipeline;
    }


    public class LogicTestingBuilder extends AbstractPipelineBuilder<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> {

        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline;
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline;
        Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> neuralTestingPipeline;

        public LogicTestingBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> buildPipeline() {
            Pipeline<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> pipeline = new Pipeline<>("LogicTestingPipeline", this);
            if (groundingPipeline == null) {
                GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
                groundingPipeline = pipeline.register(groundingBuilder.buildPipeline());

                NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, new Neuralizer(groundingBuilder.grounder));
                neuralizationPipeline = pipeline.register(neuralNetsBuilder.buildPipeline());
            }

            if (neuralTestingPipeline == null) {
                NeuralTestingBuilder neuralTestingBuilder = new NeuralTestingBuilder(settings);
                neuralTestingPipeline = pipeline.registerEnd(neuralTestingBuilder.buildPipeline());
            }

            Branch<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Pair<Template, Stream<LogicSample>>, NeuralModel> modelSplitBranch =
                    pipeline.registerStart(new Branch<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Pair<Template, Stream<LogicSample>>, NeuralModel>("ModelSplitBranch") {
                        @Override
                        protected Pair<Pair<Template, Stream<LogicSample>>, NeuralModel> branch(Pair<Pair<Template, NeuralModel>, Stream<LogicSample>> input) {
                            return new Pair<>(new Pair<>(input.r.r, input.s), input.r.s);
                        }
                    });

            modelSplitBranch.connectAfterL(groundingPipeline);

            groundingPipeline.connectAfter(neuralizationPipeline);

            PairMerge<NeuralModel, Stream<NeuralSample>> testMerge = pipeline.register(new PairMerge<>());
            testMerge.connectBeforeL(modelSplitBranch.output2);
            testMerge.connectBeforeR(neuralizationPipeline);
            testMerge.connectAfter(neuralTestingPipeline);

            return pipeline;
        }
    }

    public class NeuralTestingBuilder extends AbstractPipelineBuilder<Pair<NeuralModel, Stream<NeuralSample>>, Results> {
        public NeuralTestingBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> buildPipeline() {
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> pipeline = new Pipeline<>("NeuralTestingPipeline", this);
            NeuralEvaluationPipe neuralEvaluationPipe = pipeline.registerEnd(pipeline.registerStart(new NeuralEvaluationPipe(settings)));
            return pipeline;
        }
    }

}