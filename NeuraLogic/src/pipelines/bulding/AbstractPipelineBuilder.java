package pipelines.bulding;

import pipelines.Pipeline;
import settings.Settings;

import java.util.logging.Logger;

public abstract class AbstractPipelineBuilder<S, T> {
    private static final Logger LOG = Logger.getLogger(AbstractPipelineBuilder.class.getName());
    public Settings settings;

    public AbstractPipelineBuilder(Settings settings) {
        this.settings = settings;
    }

    public abstract Pipeline<S, T> buildPipeline();

}