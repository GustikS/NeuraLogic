package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.crossvalidation.TrainTestResults;
import networks.evaluation.results.Results;
import neuralogic.template.PlainTemplateParseTree;
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

import java.util.Optional;
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

        LearningSchemeBuilder learningSchemeBuilder = new LearningSchemeBuilder(settings, sources);
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

        Merge<Results, Results, TrainTestResults> resultsMerge = pipeline.registerEnd(new Merge<Results, Results, TrainTestResults>("TrainTestResultsMerge") {
            @Override
            protected TrainTestResults merge(Results train, Results test) {
                return new TrainTestResults(train, test);
            }
        });

        resultsMerge.connectBeforeL(pairBranch1.output2);
        resultsMerge.connectBeforeR(logicTestingPipeline);

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
            return null;
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
            return null;
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
            TrainingBuilder.NeuralLearningBuilder neuralLearningBuilder = new TrainingBuilder(settings).new NeuralLearningBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> neuralLearning = pipeline.register(neuralLearningBuilder.buildPipeline());
            TestingBuilder.NeuralTestingBuilder neuralTestingBuilder = new TestingBuilder(settings).new NeuralTestingBuilder(settings);
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> neuralTesting = pipeline.register(neuralTestingBuilder.buildPipeline());

            PairBranch<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>> modelSamplesBranch = pipeline.registerStart(new PairBranch<>());
            PairBranch<Stream<NeuralSample>, Stream<NeuralSample>> trainTestBranch = pipeline.register(new PairBranch<>());
            PairBranch<NeuralModel, Results> modelResultsBranch = pipeline.register(new PairBranch<>());
            PairMerge<NeuralModel, Stream<NeuralSample>> trainingMerge = pipeline.register(new PairMerge<>());
            PairMerge<NeuralModel, Stream<NeuralSample>> testingMerge = pipeline.register(new PairMerge<>());
            Merge<Results, Results, TrainTestResults> resultsMerge = pipeline.registerEnd(new Merge<Results, Results, TrainTestResults>("TrainTestResultsMerge") {
                @Override
                protected TrainTestResults merge(Results train, Results test) {
                    return new TrainTestResults(train, test);
                }
            });

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