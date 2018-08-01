package pipelines.bulding;

import constructs.example.LogicSample;
import learning.crossvalidation.TrainTestResults;
import pipelines.Pipe;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainTestBuilder extends AbstractPipelineBuilder<Sources, TrainTestResults> {
    private static final Logger LOG = Logger.getLogger(TrainTestBuilder.class.getName());

    LearningBuilder learningBuilder;
    TestingBuilder testingBuilder;

    public TrainTestBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Sources, TrainTestResults> buildPipeline(Sources sources) {
        Pipeline<Sources, TrainTestResults> pipeline = new Pipeline<>("TrainTestPipeline");
        SamplesProcessingBuilder samplesExtractor = new SamplesProcessingBuilder(settings);
        Pipe<Sources, Stream<LogicSample>> trainingSamplesPipe = samplesExtractor.extractTrainingSamplesPipe(sources);
        pipeline.register(trainingSamplesPipe);

        learningBuilder.buildPipeline()

        Pipe<Sources, Stream<LogicSample>> testingSamplesPipe = samplesExtractor.extractTestingSamplesPipe(sources);
        pipeline.register(testingSamplesPipe);

    }
}