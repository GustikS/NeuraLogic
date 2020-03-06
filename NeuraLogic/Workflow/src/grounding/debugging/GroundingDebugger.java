package grounding.debugging;

import constructs.building.factories.WeightFactory;
import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundingSample;
import pipelines.Pipeline;
import pipelines.debug.Debugger;
import settings.Settings;
import settings.Sources;
import grounding.debugging.drawing.GroundingDrawer;
import utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class GroundingDebugger extends Debugger<GroundingSample> {
    private static final Logger LOG = Logger.getLogger(GroundingDebugger.class.getName());
    WeightFactory weightFactory;

    public GroundingDebugger(Settings settings){
        super(settings);
        drawer = new GroundingDrawer(settings);
    }

    public GroundingDebugger(Sources sources, Settings settings) {
        super(sources, settings);
        drawer = new GroundingDrawer(settings);
    }

    @Override
    public void debug(GroundingSample sample) {
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
