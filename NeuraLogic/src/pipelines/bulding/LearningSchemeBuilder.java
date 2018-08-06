package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.crossvalidation.TrainTestResults;
import pipelines.Merge;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.prepared.pipes.generic.DuplicateBranch;
import pipelines.prepared.pipes.generic.PairMerge;
import pipelines.prepared.pipes.generic.SecondFromPairPipe;
import pipelines.prepared.pipes.specific.TemplateToNeuralPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import training.NeuralModel;
import networks.evaluation.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Builder intended for bulding Pipelines that start with the plain Sources object, i.e. at the very beginning of the program
 */
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
     * Based on provided Sources (samples) decide the learning mode and return Pipeline
     * @param sources
     * @return
     */
    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        Pipeline<Sources, Results> pipeline = new Pipeline<>("LearningSchemePipeline");

        if (sources.crossvalidation) {
            CrossvalidationBuilder crossvalidationSchemeBuilder = new CrossvalidationBuilder(settings,sources);
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings,sources);
            crossvalidationSchemeBuilder.trainTestBuilder = trainTestBuilder;
            pipeline = crossvalidationSchemeBuilder.buildPipeline();

        } else if (sources.trainTest) { //returns only test results in this case
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings, sources);
            Pipeline<Sources, TrainTestResults> trainTestPipeline = pipeline.registerStart(trainTestBuilder.buildPipeline());
            Pipe<TrainTestResults,Results> getTestResultsPipe = pipeline.registerEnd(new Pipe<TrainTestResults,Results>("GetTestResultsPipe") {
                @Override
                public Results apply(TrainTestResults trainTestResults) {
                    return trainTestResults.testing;
                }
            });
            trainTestPipeline.connectAfter(getTestResultsPipe);

        } else if (sources.trainOnly) {
            Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Results>> trainingPipeline = pipeline.registerStart(this.buildTrainingPipeline());
            SecondFromPairPipe<Pair<Template, NeuralModel>, Results> secondFromPairPipe = pipeline.registerEnd(new SecondFromPairPipe<>());
            trainingPipeline.connectAfter(secondFromPairPipe);

        } else if (sources.testOnly) {
            Pipeline<Sources, Results> testingPipeline = pipeline.registerEnd(pipeline.registerStart(this.buildTestingPipeline()));

        } else {
            LOG.severe("Invalid learning mode setting.");
        }
        return pipeline;
    }


    /**
     * Pure testing, starting from Sources (test Source + Template)
     * @return
     */
    public Pipeline<Sources, Results> buildTestingPipeline(){
        Pipeline<Sources, Results> pipeline = new Pipeline<>("TestingPipeline");
        DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>());
        Pipe<Sources, Source> sourcesSourcePipe = pipeline.register(new Pipe<Sources, Source>("sourcesSourcePipe") {
            @Override
            public Source apply(Sources sources) {
                return sources.test;
            }
        });
        SamplesProcessingBuilder testingSamplesProcessor = new SamplesProcessingBuilder(settings, sources.train);
        Pipeline<Source, Stream<LogicSample>> sourcesStreamPipeline = pipeline.register(testingSamplesProcessor.buildPipeline());

        TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);
        Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());
        DuplicateBranch<Template> duplicateBranch1 = pipeline.register(new DuplicateBranch<>());
        TemplateToNeuralPipe templateToNeuralPipe = pipeline.register(new TemplateToNeuralPipe());
        PairMerge<Pair<Template,NeuralModel>, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());
        TestingBuilder.LogicTestingBuilder testingBuilder = (new TestingBuilder(settings)).new LogicTestingBuilder(settings);
        Pipeline<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Results> logicTestingPipeline = pipeline.registerEnd(testingBuilder.buildPipeline());
        PairMerge<Template,NeuralModel> pairMerge1 = pipeline.register(new PairMerge<>());

        duplicateBranch.connectAfterL(sourcesSourcePipe);
        duplicateBranch.connectAfterR(sourcesTemplatePipeline);

        sourcesSourcePipe.connectAfter(sourcesStreamPipeline);
        sourcesTemplatePipeline.connectAfter(duplicateBranch1);
        duplicateBranch1.connectAfterL(templateToNeuralPipe);

        pairMerge1.connectBeforeL(duplicateBranch1.output2);
        pairMerge1.connectBeforeR(templateToNeuralPipe);

        pairMerge.connectBeforeL(pairMerge1);
        pairMerge.connectBeforeR(sourcesStreamPipeline);

        pairMerge.connectAfter(logicTestingPipeline);
        return pipeline;
    }


    /**
     * Pure training, starting from Sources (train Source + Template)
     * @return
     */
    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>,Results>> buildTrainingPipeline(){
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>,Results>> pipeline = new Pipeline<>("TrainingPipeline");
        DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>());
        Pipe<Sources, Source> sourcesSourcePipe = pipeline.register(new Pipe<Sources, Source>("SourcesSourcePipe") {
            @Override
            public Source apply(Sources sources) {
                return sources.train;
            }
        });
        duplicateBranch.connectAfterL(sourcesSourcePipe);
        SamplesProcessingBuilder trainingSamplesProcessor = new SamplesProcessingBuilder(settings, sources.train);
        Pipeline<Source, Stream<LogicSample>> sourcesStreamPipeline = pipeline.register(trainingSamplesProcessor.buildPipeline());
        sourcesSourcePipe.connectAfter(sourcesStreamPipeline);

        TrainingBuilder trainingBuilder = new TrainingBuilder(settings);
        if (sources.templateProvided) {
            TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);
            Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());
            DuplicateBranch<Template> duplicateBranch1 = pipeline.register(new DuplicateBranch<>());

            PairMerge<Template, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());
            TrainingBuilder.LogicLearningBuilder logicLearningBuilder = trainingBuilder.new LogicLearningBuilder(settings);
            Pipeline<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> pairPairPipeline = pipeline.register(logicLearningBuilder.buildPipeline());

            sourcesTemplatePipeline.connectAfter(duplicateBranch1);

            duplicateBranch.connectAfterR(sourcesTemplatePipeline);

            pairMerge.connectBeforeL(duplicateBranch1.output2);
            pairMerge.connectBeforeR(sourcesStreamPipeline);
            pairMerge.connectAfter(pairPairPipeline);

            Merge<Template,Pair<NeuralModel,Results>,Pair<Pair<Template, NeuralModel>,Results>> finalMerge = pipeline.registerEnd(new Merge<Template,Pair<NeuralModel,Results>,Pair<Pair<Template, NeuralModel>,Results>>("ModelMerge"){
                @Override
                protected Pair<Pair<Template, NeuralModel>, Results> merge(Template input1, Pair<NeuralModel, Results> input2) {
                    return new Pair<>(new Pair<>(input1,input2.r),input2.s);
                }
            });
            finalMerge.connectBeforeL(duplicateBranch1.output1);
            finalMerge.connectBeforeR(pairPairPipeline);

        } else {
            TrainingBuilder.StructureLearningBuilder structureLearningBuilder = trainingBuilder.new StructureLearningBuilder(settings);
            Pipeline<Stream<LogicSample>, Pair<Pair<Template,NeuralModel>, Results>> streamPairPipeline = pipeline.registerEnd(structureLearningBuilder.buildPipeline());
        }
        return pipeline;
    }
}