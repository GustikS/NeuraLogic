package building;

import constructs.template.Template;
import constructs.template.debugging.TemplateDebugger;
import grounding.debugging.GroundingDebugger;
import learning.crossvalidation.TrainTestResults;
import learning.results.Progress;
import learning.results.Results;
import networks.computation.debugging.TrainingDebugger;
import networks.computation.training.NeuralModel;
import networks.structure.building.debugging.NeuralDebugger;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.bulding.AbstractPipelineBuilder;
import pipelines.debug.drawing.PipelineDebugger;
import pipelines.pipes.generic.SecondFromPairPipe;
import pipes.specific.ResultsFromProgressPipe;
import settings.Settings;
import settings.Sources;
import utils.generic.Pair;

import java.util.logging.Logger;

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

    /**
     * Get any pipeline starting from sources
     *
     * @param settings
     * @param sources
     * @return
     */
    public static Pipeline<Sources, ?> getPipeline(Settings settings, Sources sources) {
        LOG.finest("Building pipeline from sources and settings...");
        AbstractPipelineBuilder<Sources, ?> pipelineBuilder = LearningSchemeBuilder.getBuilder(sources, settings);
        Pipeline<Sources, ?> pipeline = pipelineBuilder.buildPipeline();
        if (settings.debugPipeline) {
            new PipelineDebugger(settings).debug(pipeline);
        }
        LOG.finest("The main pipeline has been built");
        return pipeline;
    }

    /**
     * Any builder starting from Sources
     *
     * @param sources
     * @param settings
     * @return
     */
    public static AbstractPipelineBuilder<Sources, ?> getBuilder(Sources sources, Settings settings) {
        if (settings.mainMode == Settings.MainMode.COMPLETE) {
            return new LearningSchemeBuilder(settings, sources);
        } else if (settings.mainMode == Settings.MainMode.DEBUGGING) {
            if (settings.debugTemplateTraining || settings.debugSampleTraining) {
                return new TrainingDebugger(sources, settings);
            } else if (settings.debugNeuralization) {
                return new NeuralDebugger(sources, settings);
            } else if (settings.debugGrounding) {
                return new GroundingDebugger(sources, settings);
            } else if (settings.debugTemplate) {
                return new TemplateDebugger(sources, settings);
            }
        } else if (settings.mainMode == Settings.MainMode.NEURALIZATION) {
            return new End2endTrainigBuilder(settings, sources).new End2endNNBuilder();
        }
        LOG.severe("Unknown pipeline mainMode!");
        throw new UnsupportedOperationException();
    }

    @Override
    public Pipeline<Sources, Results> buildPipeline() {
        return buildPipeline(this.sources);
    }

    /**
     * Based on provided Sources (samples) decide the learning mode and return corresponding Pipeline
     *
     * @param sources
     * @return
     */
    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        Pipeline<Sources, Results> pipeline = new Pipeline<>("LearningSchemePipeline", this);

        if (sources.crossvalidation) { //returns only test results in this case
            LOG.info("Learning scheme inferred as : crossvalidation.");
            CrossvalidationBuilder crossvalidationSchemeBuilder = new CrossvalidationBuilder(settings, sources);
            Pipeline<Sources, TrainTestResults> crossvalPipeline = pipeline.registerStart(crossvalidationSchemeBuilder.buildPipeline());
            crossvalPipeline.connectAfter(pipeline.registerEnd(getTestResultsPipe()));

        } else if (sources.trainTest) { //returns only test results in this case
            LOG.info("Learning scheme inferred as : train-test.");
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings, sources);
            Pipeline<Sources, TrainTestResults> trainTestPipeline = pipeline.registerStart(trainTestBuilder.buildPipeline());
            trainTestPipeline.connectAfter(pipeline.registerEnd(getTestResultsPipe()));

        } else if (sources.trainOnly) {
            LOG.info("Learning scheme inferred as : pure training.");
            Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> trainingPipeline = pipeline.registerStart(new TrainingBuilder(settings, sources).buildPipeline());
            SecondFromPairPipe<Pair<Template, NeuralModel>, Progress> secondFromPairPipe = pipeline.register(new SecondFromPairPipe<>());
            trainingPipeline.connectAfter(secondFromPairPipe);
            ResultsFromProgressPipe resultsFromProgressPipe = pipeline.registerEnd(new ResultsFromProgressPipe());
            secondFromPairPipe.connectAfter(resultsFromProgressPipe);

        } else if (sources.testOnly) {
            LOG.info("Learning scheme inferred as : pure testing.");
            Pipeline<Sources, Results> testingPipeline = pipeline.registerEnd(pipeline.registerStart(new TestingBuilder(settings, sources).buildPipeline()));

        } else {
            LOG.severe("Invalid learning mode setting.");
        }
        return pipeline;
    }

    Pipe<TrainTestResults, Results> getTestResultsPipe() {
        return new Pipe<TrainTestResults, Results>("GetTestResultsPipe") {
            @Override
            public Results apply(TrainTestResults trainTestResults) {
                return trainTestResults.testing;
            }
        };
    }
}