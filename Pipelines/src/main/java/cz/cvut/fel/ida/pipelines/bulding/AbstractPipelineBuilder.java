package cz.cvut.fel.ida.pipelines.bulding;

import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.pipelines.Pipeline;

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