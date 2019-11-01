package pipelines.building;

import constructs.building.debugging.TemplateDebugger;
import grounding.debugging.GroundingDebugger;
import networks.computation.training.debugging.TrainingDebugger;
import networks.structure.building.debugging.NeuralDebugger;
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

    /**
     * Any builder starting from Sources
     *
     * @param sources
     * @param settings
     * @return
     */
    public static AbstractPipelineBuilder<Sources, ?> getBuilder(Sources sources, Settings settings) {
        if (settings.mainMode == Settings.MainMode.COMPLETE) {
            return new LearningSchemeBuilder(settings, sources);
        } else if (settings.mainMode == Settings.MainMode.DEBUGGING) {
            if (settings.debugTemplateTraining || settings.debugSampleTraining) {
                return new TrainingDebugger(sources, settings);
            } else if (settings.debugNeuralization) {
                return new NeuralDebugger(sources, settings);
            } else if (settings.debugGrounding) {
                return new GroundingDebugger(sources, settings);
            } else if (settings.debugTemplate) {
                return new TemplateDebugger(sources, settings);
            }
        } else if (settings.mainMode == Settings.MainMode.NEURALIZATION) {
            return new End2endTrainigBuilder(settings, sources).new End2endNNBuilder();
        }
        LOG.severe("Unknown pipeline mainMode!");
        throw new UnsupportedOperationException();
    }
}