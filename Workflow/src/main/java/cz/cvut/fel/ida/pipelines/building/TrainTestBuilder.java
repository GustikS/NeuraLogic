package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.learning.crossvalidation.TrainTestResults;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.learning.results.Results;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.pipelines.Merge;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.pipes.generic.DuplicateBranch;
import cz.cvut.fel.ida.pipelines.pipes.generic.PairBranch;
import cz.cvut.fel.ida.pipelines.pipes.generic.PairMerge;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Source;
import cz.cvut.fel.ida.setup.Sources;

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

    Merge<Progress, Results, TrainTestResults> resultsMerge;

    public TrainTestBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;

        trainingBuilder = new TrainingBuilder(settings, sources);
        testingBuilder = new TestingBuilder(settings, sources);

        resultsMerge = new Merge<Progress, Results, TrainTestResults>("TrainTestResultsMerge", settings) {
            @Override
            protected TrainTestResults merge(Progress train, Results test) {
                return new TrainTestResults(train, test);
            }
        };
    }

    @Override
    public Pipeline<Sources, TrainTestResults> buildPipeline() {
        return buildPipeline(sources);
    }

    public Pipeline<Sources, TrainTestResults> buildPipeline(Sources sources) {
        Pipeline<Sources, TrainTestResults> pipeline = new Pipeline<>("TrainTestPipeline", this);

        DuplicateBranch<Sources> duplicateSources = pipeline.registerStart(new DuplicateBranch<>());
        Pipe<Sources, Source> testExtrPipe = pipeline.register(new Pipe<Sources, Source>("TestExtractionPipe") {
            @Override
            public Source apply(Sources sources) {
                return sources.test;
            }
        });

        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> trainingPipeline = pipeline.register(trainingBuilder.buildPipeline());
        PairBranch<Pair<Template, NeuralModel>, Progress> pairBranch1 = pipeline.register(new PairBranch<>());
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
            Pipeline<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults> pipeline = new Pipeline<>("LogicTrainTestPipeline", this);

            PairBranch<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>> templateSamplesBranch = pipeline.registerStart(new PairBranch<>());
            PairBranch<Stream<LogicSample>, Stream<LogicSample>> trainTestBranch = pipeline.register(new PairBranch<>());
            PairMerge<Template, Stream<LogicSample>> trainMerge = pipeline.register(new PairMerge<>());
            PairMerge<Pair<Template, NeuralModel>, Stream<LogicSample>> testMerge = pipeline.register(new PairMerge<>());
            PairBranch<Pair<Template, NeuralModel>, Progress> trainResultsBranch = pipeline.register(new PairBranch<>());

            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<Pair<Template, NeuralModel>, Progress>> trainingPipeline = pipeline.register(trainingBuilder.new LogicLearningBuilder(settings).buildPipeline());

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

        //public Pipeline<Pair<PlainTemplateParseTree, ValuePair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults> buildTemplatePipeline() {
        //}

    }

    public class StructureTrainTestBuilder extends AbstractPipelineBuilder<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults> {


        public StructureTrainTestBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults> buildPipeline() {
            Pipeline<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults> pipeline = new Pipeline<>("StructureTrainTestPipeline", this);

            PairBranch<Stream<LogicSample>, Stream<LogicSample>> trainTestBranch = pipeline.registerStart(new PairBranch<>());
            Pipeline<Stream<LogicSample>, Pair<Pair<Template, NeuralModel>, Progress>> trainingPipeline = pipeline.register(new TrainingBuilder.StructureLearningBuilder(settings).buildPipeline());

            Pipeline<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> testingPipeline = pipeline.register(testingBuilder.new LogicTestingBuilder(settings).buildPipeline());

            PairBranch<Pair<Template, NeuralModel>, Progress> trainResultsBranch = pipeline.register(new PairBranch<>());
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
            Pipeline<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>, TrainTestResults> pipeline = new Pipeline<>("NeuralTrainTestPipeline", this);
            TrainingBuilder.NeuralLearningBuilder neuralLearningBuilder = new TrainingBuilder.NeuralLearningBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> neuralLearning = pipeline.register(neuralLearningBuilder.buildPipeline());
            TestingBuilder.NeuralTestingBuilder neuralTestingBuilder = new TestingBuilder(settings, sources).new NeuralTestingBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> neuralTesting = pipeline.register(neuralTestingBuilder.buildPipeline());

            PairBranch<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>> modelSamplesBranch = pipeline.registerStart(new PairBranch<>());
            PairBranch<Stream<NeuralSample>, Stream<NeuralSample>> trainTestBranch = pipeline.register(new PairBranch<>());
            PairBranch<NeuralModel, Progress> modelResultsBranch = pipeline.register(new PairBranch<>());
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

            pipeline.registerEnd(resultsMerge);

            return pipeline;
        }
    }
}