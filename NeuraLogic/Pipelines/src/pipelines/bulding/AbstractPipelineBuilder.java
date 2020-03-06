package pipelines.bulding;

import pipelines.Pipeline;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractPipelineBuilder<S, T> {
    private static final Logger LOG = Logger.getLogger(AbstractPipelineBuilder.class.getName());
    public Settings settings;

    public AbstractPipelineBuilder(Settings settings) {
        this.settings = settings;
    }

    public abstract Pipeline<S, T> buildPipeline();

    public List<Pipeline<S, T>> buildPipelines(int count) {
        List<Pipeline<S, T>> pipelines = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Pipeline<S, T> ppln = buildPipeline();
            ppln.ID += i;
            pipelines.add(ppln);
        }
        return pipelines;
    }

}