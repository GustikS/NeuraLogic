package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.crossvalidation.TrainTestResults;
import networks.evaluation.results.Results;
import pipelines.Merge;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.prepared.pipes.generic.DuplicateBranch;
import pipelines.prepared.pipes.generic.PairBranch;
import pipelines.prepared.pipes.generic.PairMerge;
import settings.Settings;
import settings.Source;
import settings.Sources;
import training.NeuralModel;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainTestBuilder extends AbstractPipelineBuilder<Sources, TrainTestResults> {
    private static final Logger LOG = Logger.getLogger(TrainTestBuilder.class.getName());
    private final Sources sources;

    TrainingBuilder trainingBuilder;
    TestingBuilder testingBuilder;

    public TrainTestBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, TrainTestResults> buildPipeline() {
        return buildPipeline(sources);
    }

    public Pipeline<Sources, TrainTestResults> buildPipeline(Sources sources) {
        Pipeline<Sources, TrainTestResults> pipeline = new Pipeline<>("TrainTestPipeline");

        DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>());
        Pipe<Sources, Source> testExtrPipe = pipeline.register(new Pipe<Sources, Source>("TestExtractionPipe") {
            @Override
            public Source apply(Sources sources) {
                return sources.test;
            }
        });

        LearningSchemeBuilder learningSchemeBuilder = new LearningSchemeBuilder(settings,sources);
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Results>> trainingPipeline = pipeline.register(learningSchemeBuilder.buildTrainingPipeline());
        PairBranch<Pair<Template, NeuralModel>, Results> pairBranch1 = pipeline.register(new PairBranch<>());

        TestingBuilder.LogicTestingBuilder logicTestingBuilder = new TestingBuilder(settings).new LogicTestingBuilder(settings);
        Pipeline<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> logicTestingPipeline = pipeline.register(logicTestingBuilder.buildPipeline());

        SamplesProcessingBuilder samplesExtractor = new SamplesProcessingBuilder(settings, sources.train);
        Pipeline<Source, Stream<LogicSample>> testSourceStreamPipeline = pipeline.register(samplesExtractor.buildPipeline(sources.test));

        PairMerge<Pair<Template, NeuralModel>, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());

        duplicateBranch.connectAfterL(testExtrPipe);
        duplicateBranch.connectAfterR(trainingPipeline);

        testExtrPipe.connectAfter(testSourceStreamPipeline);

        trainingPipeline.connectAfter(pairBranch1);

        pairMerge.connectBeforeL(pairBranch1.output1);
        pairMerge.connectBeforeR(testSourceStreamPipeline);

        pairMerge.connectAfter(logicTestingPipeline);

        Merge<Results,Results,TrainTestResults> resultsMerge = pipeline.registerEnd(new Merge<Results, Results, TrainTestResults>() {
            @Override
            protected TrainTestResults merge(Results train, Results test) {
                return new TrainTestResults(train, test);
            }
        });

        resultsMerge.connectBeforeL(pairBranch1.output2);
        resultsMerge.connectBeforeR(logicTestingPipeline);

        return pipeline;
    }

    public Pipeline<Pair<NeuralModel,Pair<Stream<NeuralSample>,Stream<NeuralSample>>>, TrainTestResults> buildNeuralPipeline() {
        //TODO next
    }
}