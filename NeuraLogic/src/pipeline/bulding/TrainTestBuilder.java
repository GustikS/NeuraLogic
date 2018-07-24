package pipeline.bulding;

import learning.LearningSample;
import learning.crossvalidation.TrainTestResults;
import pipeline.Pipe;
import pipeline.Pipeline;
import pipeline.bulding.pipes.SamplesProcessor;
import settings.Settings;
import settings.Sources;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainTestBuilder extends AbstractPipelineBuilder<Sources, TrainTestResults> {
    private static final Logger LOG = Logger.getLogger(TrainTestBuilder.class.getName());

    TrainingBuilder trainingBuilder;
    TestingBuilder testingBuilder;

    public TrainTestBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Sources, TrainTestResults> buildPipeline(Sources sources) {
        Pipeline<Sources, TrainTestResults> pipeline = new Pipeline<>("TrainTestPipeline");
        SamplesProcessor samplesExtractor = new SamplesProcessor(settings);
        Pipe<Sources, Stream<LearningSample>> trainingSamplesPipe = samplesExtractor.extractTrainingSamplesPipe(sources);
        pipeline.register(trainingSamplesPipe);

        trainingBuilder.buildPipeline()

        Pipe<Sources, Stream<LearningSample>> testingSamplesPipe = samplesExtractor.extractTestingSamplesPipe(sources);
        pipeline.register(testingSamplesPipe);

    }
}