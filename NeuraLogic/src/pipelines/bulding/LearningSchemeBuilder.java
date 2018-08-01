package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.crossvalidation.TrainTestResults;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.prepared.pipes.generic.DuplicateBranch;
import pipelines.prepared.pipes.generic.PairMerge;
import pipelines.prepared.pipes.generic.SecondFromPairPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import training.NeuralModel;
import networks.evaluation.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class LearningSchemeBuilder extends AbstractPipelineBuilder<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(LearningSchemeBuilder.class.getName());
    private Sources sources;

    public LearningSchemeBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, Results> buildPipeline() {
        return buildPipeline(this.sources);
    }

    /**
     * Based on provided sources (samples) decide the learning mode and return Pipeline
     * @param sources
     * @return
     */
    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        Pipeline<Sources, Results> pipeline = new Pipeline<>("LearningSchemePipeline");

        if (sources.crossvalidation) {
            CrossvalidationBuilder crossvalidationSchemeBuilder = new CrossvalidationBuilder(settings);
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings);
            crossvalidationSchemeBuilder.trainTestBuilder = trainTestBuilder;
            Pipeline<Sources, Results> sourcesResultsPipeline = crossvalidationSchemeBuilder.buildPipeline(sources);
            return sourcesResultsPipeline;
        } else if (sources.trainTest) {
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings);
            Pipeline<Sources, TrainTestResults> pipeline2 = trainTestBuilder.buildPipeline(sources);
        } else if (sources.trainOnly) {
            DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>());
            Pipe<Sources, Source> sourcesSourcePipe = pipeline.register(new Pipe<Sources, Source>("sourcesSourcePipe") {
                @Override
                public Source apply(Sources sources) {
                    return sources.train;
                }
            });
            duplicateBranch.connectAfterL(sourcesSourcePipe);
            SamplesProcessingBuilder trainingSamplesProcessor = new SamplesProcessingBuilder(settings, sources.train);
            Pipeline<Source, Stream<LogicSample>> sourcesStreamPipeline = pipeline.register(trainingSamplesProcessor.buildPipeline());
            sourcesSourcePipe.connectAfter(sourcesStreamPipeline);

            LearningBuilder learningBuilder = new LearningBuilder(settings);
            if (sources.templateProvided) {
                TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);
                Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());
                duplicateBranch.connectAfterR(sourcesTemplatePipeline);
                PairMerge<Template, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());
                LearningBuilder.NormalLearningBuilder normalLearningBuilder = learningBuilder.new NormalLearningBuilder(settings);
                Pipeline<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> pairPairPipeline = pipeline.register(normalLearningBuilder.buildPipeline());
                pairMerge.connectAfter(pairPairPipeline);
                SecondFromPairPipe<NeuralModel, Results> secondFromPairPipe = pipeline.registerEnd(new SecondFromPairPipe<>());
                pairPairPipeline.connectAfter(secondFromPairPipe);
            } else {
                LearningBuilder.StructureLearningBuilder structureLearningBuilder = learningBuilder.new StructureLearningBuilder(settings);
                Pipeline<Stream<LogicSample>, Pair<Template, Results>> streamPairPipeline = pipeline.register(structureLearningBuilder.buildPipeline());
                SecondFromPairPipe<Template, Results> secondFromPairPipe = pipeline.registerEnd(new SecondFromPairPipe<>());
                streamPairPipeline.connectAfter(secondFromPairPipe);
            }

        } else if (sources.testOnly) {
            TestingBuilder testingBuilder = new TestingBuilder(settings);
        } else {
            LOG.warning("Invalid learning mode setting.");
        }
        return pipeline;
    }
}