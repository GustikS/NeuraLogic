package pipeline;

import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.logging.Logger;

public class PipelineBuilder {
    private static final Logger LOG = Logger.getLogger(PipelineBuilder.class.getName());
    Settings settings;
    Sources sources;

    public PipelineBuilder(Settings settings, Sources sources) {
        this.settings = settings;
        this.sources = sources;
    }

    public Pipeline<Sources, Results> buildPipeline() {
       //TODO next

    }
}