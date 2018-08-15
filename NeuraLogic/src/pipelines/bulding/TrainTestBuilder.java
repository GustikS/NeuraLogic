package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.crossvalidation.TrainTestResults;
import networks.evaluation.results.Results;
import pipelines.Merge;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.pipes.generic.DuplicateBranch;
import pipelines.pipes.generic.PairBranch;
import pipelines.pipes.generic.PairMerge;
import settings.Settings;
import settings.Source;
import settings.Sources;
import training.NeuralModel;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Performs train+test in different sample/template representations.
 * Makes sure to connect train+test routines, samples, and results correctly
 */
public class TrainTestBuilder extends AbstractPipelineBuilder<Sources, TrainTestResults> {
    private static final Logger LOG = Logger.getLogger(TrainTestBuilder.class.getName());
    private final Sources sources;

    TrainingBuilder trainingBuilder;
    TestingBuilder testingBuilder;

    Merge<Results, Results, TrainTestResults> resultsMerge;

    public TrainTestBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;

        trainingBuilder = new TrainingBuilder(settings, sources);
        testingBuilder = new TestingBuilder(settings, sources);

        resultsMerge = new Merge<Results, Results, TrainTestResults>("TrainTestResultsMerge") {
            @Override
            protected TrainTestResults merge(Results train, Results test) {
                return new TrainTestResults(train, test);
            }
        };
    }

    @Override
    public Pipeline<Sources, TrainTestResults> buildPipeline() {
        return buildPipeline(sources);
    }

    public Pipeline<Sources, TrainTestResults> buildPipeline(Sources sources) {
        Pipeline<Sources, TrainTestResults> pipeline = new Pipeline<>("TrainTestPipeline");

        DuplicateBranch<Sources> duplicateSources = pipeline.registerStart(new DuplicateBranch<>());
        Pipe<Sources, Source> testExtrPipe = pipeline.register(new Pipe<Sources, Source>("TestExtractionPipe") {
            @Override
            public Source apply(Sources sources) {
                return sources.test;
            }
        });

        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Results>> trainingPipeline = pipeline.register(trainingBuilder.buildPipeline());
        PairBranch<Pair<Template, NeuralModel>, Results> pairBranch1 = pipeline.register(new PairBranch<>());
        Pipeline<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> logicTestingPipeline = pipeline.register(testingBuilder.new LogicTestingBuilder(settings).buildPipeline());
        Pipeline<Source, Stream<LogicSample>> testSamplesStreamPipeline = pipeline.register(new SamplesProcessingBuilder(settings, sources.test).buildPipeline(sources.test));
        PairMerge<Pair<Template, NeuralModel>, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());


        duplicateSources.connectAfterL(testExtrPipe);
        duplicateSources.connectAfterR(trainingPipeline);

        testExtrPipe.connectAfter(testSamplesStreamPipeline);

        trainingPipeline.connectAfter(pairBranch1);

        pairMerge.connectBeforeL(pairBranch1.output1);
        pairMerge.connectBeforeR(testSamplesStreamPipeline);

        pairMerge.connectAfter(logicTestingPipeline);

        resultsMerge.connectBeforeL(pairBranch1.output2);
        resultsMerge.connectBeforeR(logicTestingPipeline);
        pipeline.registerEnd(resultsMerge);

        return pipeline;
    }

    public class LogicTrainTestBuilder extends AbstractPipelineBuilder<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults> {

        public LogicTrainTestBuilder(Settings settings) {
            super(settings);
        }

        /**
         * First Stream<NeuralSample> is Train and second is Test
         *
         * @return
         */
        @Override
        public Pipeline<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults> buildPipeline() {
            Pipeline<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults> pipeline = new Pipeline<>("LogicTrainTestPipeline");

            PairBranch<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>> templateSamplesBranch = pipeline.registerStart(new PairBranch<>());
            PairBranch<Stream<LogicSample>, Stream<LogicSample>> trainTestBranch = pipeline.register(new PairBranch<>());
            PairMerge<Template, Stream<LogicSample>> trainMerge = pipeline.register(new PairMerge<>());
            PairMerge<Pair<Template, NeuralModel>, Stream<LogicSample>> testMerge = pipeline.register(new PairMerge<>());
            PairBranch<Pair<Template, NeuralModel>, Results> trainResultsBranch = pipeline.register(new PairBranch<>());

            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Results>> trainingPipeline = pipeline.register(trainingBuilder.new LogicLearningBuilder(settings).buildPipeline());

            Pipeline<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> testingPipeline = pipeline.register(testingBuilder.new LogicTestingBuilder(settings).buildPipeline());


            templateSamplesBranch.connectAfterR(trainTestBranch);
            trainMerge.connectBeforeL(templateSamplesBranch.output1);
            trainMerge.connectBeforeR(trainTestBranch.output1);
            trainMerge.connectAfter(trainingPipeline);
            trainingPipeline.connectAfter(trainResultsBranch);

            testMerge.connectBeforeL(trainResultsBranch.output1);
            testMerge.connectBeforeR(trainTestBranch.output2);
            testMerge.connectAfter(testingPipeline);

            resultsMerge.connectBeforeL(trainResultsBranch.output2);
            resultsMerge.connectBeforeR(testingPipeline);

            pipeline.registerEnd(resultsMerge);

            return pipeline;
        }

        //public Pipeline<Pair<PlainTemplateParseTree, Pair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults> buildTemplatePipeline() {
        //}

    }

    public class StructureTrainTestBuilder extends AbstractPipelineBuilder<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults> {


        public StructureTrainTestBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults> buildPipeline() {
            Pipeline<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults> pipeline = new Pipeline<>("StructureTrainTestPipeline");

            PairBranch<Stream<LogicSample>, Stream<LogicSample>> trainTestBranch = pipeline.registerStart(new PairBranch<>());
            Pipeline<Stream<LogicSample>, Pair<Pair<Template, NeuralModel>, Results>> trainingPipeline = pipeline.register(trainingBuilder.new StructureLearningBuilder(settings).buildPipeline());

            Pipeline<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> testingPipeline = pipeline.register(testingBuilder.new LogicTestingBuilder(settings).buildPipeline());

            PairBranch<Pair<Template, NeuralModel>, Results> trainResultsBranch = pipeline.register(new PairBranch<>());
            PairMerge<Pair<Template, NeuralModel>, Stream<LogicSample>> testMerge = pipeline.register(new PairMerge<>());


            trainTestBranch.connectAfterL(trainingPipeline);
            trainingPipeline.connectAfter(trainResultsBranch);
            testMerge.connectBeforeL(trainResultsBranch.output1);
            testMerge.connectBeforeR(trainTestBranch.output2);
            testMerge.connectAfter(testingPipeline);

            resultsMerge.connectBeforeL(trainResultsBranch.output2);
            resultsMerge.connectBeforeR(testingPipeline);

            pipeline.registerEnd(resultsMerge);

            return pipeline;
        }
    }

    public class NeuralTrainTestBuilder extends AbstractPipelineBuilder<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>, TrainTestResults> {

        public NeuralTrainTestBuilder(Settings settings) {
            super(settings);
        }

        /**
         * First Stream<NeuralSample> is Train and second is Test
         *
         * @return
         */
        @Override
        public Pipeline<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>, TrainTestResults> buildPipeline() {
            Pipeline<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>, TrainTestResults> pipeline = new Pipeline<>("NeuralTrainTestPipeline");
            TrainingBuilder.NeuralLearningBuilder neuralLearningBuilder = new TrainingBuilder(settings, sources).new NeuralLearningBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> neuralLearning = pipeline.register(neuralLearningBuilder.buildPipeline());
            TestingBuilder.NeuralTestingBuilder neuralTestingBuilder = new TestingBuilder(settings, sources).new NeuralTestingBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> neuralTesting = pipeline.register(neuralTestingBuilder.buildPipeline());

            PairBranch<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>> modelSamplesBranch = pipeline.registerStart(new PairBranch<>());
            PairBranch<Stream<NeuralSample>, Stream<NeuralSample>> trainTestBranch = pipeline.register(new PairBranch<>());
            PairBranch<NeuralModel, Results> modelResultsBranch = pipeline.register(new PairBranch<>());
            PairMerge<NeuralModel, Stream<NeuralSample>> trainingMerge = pipeline.register(new PairMerge<>());
            PairMerge<NeuralModel, Stream<NeuralSample>> testingMerge = pipeline.register(new PairMerge<>());

            pipeline.registerStart(modelSamplesBranch);
            modelSamplesBranch.connectAfterL(trainingMerge.input1);
            modelSamplesBranch.connectAfterR(trainTestBranch);
            trainingMerge.connectBeforeR(trainTestBranch.output1);
            trainingMerge.connectAfter(neuralLearning);
            neuralLearning.connectAfter(modelResultsBranch);
            testingMerge.connectBeforeL(modelResultsBranch.output1);
            testingMerge.connectBeforeR(trainTestBranch.output2);
            testingMerge.connectAfter(neuralTesting);
            resultsMerge.connectBeforeL(modelResultsBranch.output2);
            resultsMerge.connectBeforeR(neuralTesting);

            return pipeline;
        }
    }
}