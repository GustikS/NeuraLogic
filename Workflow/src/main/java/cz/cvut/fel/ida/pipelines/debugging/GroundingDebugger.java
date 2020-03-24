package cz.cvut.fel.ida.pipelines.debugging;

import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.debugging.drawing.GroundingDrawer;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class GroundingDebugger extends End2EndDebugger<GroundingSample> {
    private static final Logger LOG = Logger.getLogger(GroundingDebugger.class.getName());
    WeightFactory weightFactory;

    public GroundingDebugger(Settings settings) {
        super(settings);
        if (drawing)
            drawer = new GroundingDrawer(settings);
    }

    public GroundingDebugger(Sources sources, Settings settings) {
        super(sources, settings);
        if (drawing)
            drawer = new GroundingDrawer(settings);
    }

    @Override
    public void debug(GroundingSample sample) {
        LOG.fine(sample.toString());
        if (drawing)
            drawer.draw(sample);
    }

    public WeightFactory getWeightFactory() {
        return weightFactory;
    }

    @Override
    public Pipeline<Sources, Stream<GroundingSample>> buildPipeline() {
        Pipeline<Sources, Pair<Template, Stream<LogicSample>>> sourcesPairPipeline = this.pipeline.registerStart(this.end2endTrainigBuilder.buildFromSources(sources, settings));
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = this.pipeline.registerEnd(this.end2endTrainigBuilder.buildGrounding(settings, weightFactory));
        sourcesPairPipeline.connectAfter(groundingPipeline);
        return pipeline;
    }
}
