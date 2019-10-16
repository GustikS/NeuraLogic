package pipelines.building;

import networks.computation.evaluation.results.Results;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;

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

    public static AbstractPipelineBuilder<Sources, Results> getBuilder(Sources sources, Settings settings) {
        if (settings.mainMode == Settings.MainMode.COMPLETE) {
            return new LearningSchemeBuilder(settings, sources);
        } else {
            LOG.severe("Unknown pipeline mainMode!");
            throw new UnsupportedOperationException();
            //System.exit(3);
        }
    }

}