package pipeline.bulding;

import pipeline.Pipeline;
import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.logging.Logger;

public class LearningResultsPipelineBuilder extends AbstractPipelineBuilder<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(LearningResultsPipelineBuilder.class.getName());

    public LearningResultsPipelineBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        SamplesExtractor samplesExtractor = new SamplesExtractor(settings);

        if (settings.structureLearning){
            //TODO
        }
        else if (settings.onlineGrounding) {
            //TODO
        }
        else {
            // train + test

        }

    }
}
