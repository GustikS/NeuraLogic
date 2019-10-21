package pipelines.building;

import constructs.template.Template;
import learning.crossvalidation.TrainTestResults;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Results;
import networks.computation.training.NeuralModel;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.pipes.generic.SecondFromPairPipe;
import pipelines.pipes.specific.ResultsFromProgressPipe;
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