package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.learning.crossvalidation.MeanStdResults;
import cz.cvut.fel.ida.learning.crossvalidation.TrainTestResults;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.learning.results.Results;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.debugging.GroundingDebugger;
import cz.cvut.fel.ida.pipelines.debugging.NeuralDebugger;
import cz.cvut.fel.ida.pipelines.debugging.TemplateDebugger;
import cz.cvut.fel.ida.pipelines.debugging.TrainingDebugger;
import cz.cvut.fel.ida.pipelines.debuging.drawing.PipelineDebugger;
import cz.cvut.fel.ida.pipelines.pipes.generic.SecondFromPairPipe;
import cz.cvut.fel.ida.pipelines.pipes.specific.ResultsFromProgressPipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;

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
        throw new UnsupportedOperationException("Unknown pipeline mainMode! Invalid source files or settings (turn on debugging?).");
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

        LOG.finer("------------------------------------------WORKFLOW SETUP-------------------------------------------------------");

        if (sources.crossvalidation) { //returns only test results in this case
            LOG.fine("Learning scheme inferred as : ---CROSSVALIDATION---");
            CrossvalidationBuilder crossvalidationSchemeBuilder = new CrossvalidationBuilder(settings, sources);
            Pipeline<Sources, Pair<MeanStdResults.TrainValTest, TrainTestResults>> crossvalPipeline = pipeline.registerStart(crossvalidationSchemeBuilder.buildPipeline());
            Pipe pipe = crossvalPipeline.connectAfter(pipeline.register(new SecondFromPairPipe()));
            pipe.connectAfter(pipeline.registerEnd(getTestResultsPipe()));

        } else if (sources.trainTest) { //returns only test results in this case
            if (sources.trainValTest)
                LOG.fine("Learning scheme inferred as : ---TRAIN-VAL-TEST---");
            else
                LOG.fine("Learning scheme inferred as : ---TRAIN-TEST---");
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings, sources);
            Pipeline<Sources, TrainTestResults> trainTestPipeline = pipeline.registerStart(trainTestBuilder.buildPipeline());
            trainTestPipeline.connectAfter(pipeline.registerEnd(getTestResultsPipe()));

        } else if (sources.trainOnly) {
            LOG.fine("Learning scheme inferred as : ---TRAINING ONLY---");
            Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> trainingPipeline = pipeline.registerStart(new TrainingBuilder(settings, sources).buildPipeline());
            SecondFromPairPipe<Pair<Template, NeuralModel>, Progress> secondFromPairPipe = pipeline.register(new SecondFromPairPipe<>());
            trainingPipeline.connectAfter(secondFromPairPipe);
            ResultsFromProgressPipe resultsFromProgressPipe = pipeline.registerEnd(new ResultsFromProgressPipe());
            secondFromPairPipe.connectAfter(resultsFromProgressPipe);

        } else if (sources.testOnly) {
            LOG.fine("Learning scheme inferred as : ---TESTING ONLY---");
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