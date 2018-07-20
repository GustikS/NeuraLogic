package pipeline.bulding;

import learning.LearningSample;
import pipeline.Pipeline;
import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class LearningPipelineBuilder extends AbstractPipelineBuilder<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(LearningPipelineBuilder.class.getName());

    public LearningPipelineBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        SamplesExtractor samplesExtractor = new SamplesExtractor(settings);
        Pipeline<Sources, Stream<LearningSample>> sourcesStreamPipeline = samplesExtractor.buildPipeline(sources);
    }
}
