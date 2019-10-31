package grounding.debugging;

import constructs.building.factories.WeightFactory;
import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundingSample;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;
import utils.PipelineDebugger;
import utils.drawing.GroundingDrawer;
import utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class GroundingDebugger extends PipelineDebugger<GroundingSample> {
    private static final Logger LOG = Logger.getLogger(GroundingDebugger.class.getName());
    WeightFactory weightFactory;

    public GroundingDebugger(String[] args, Settings settings) {
        super(args, settings);
        //to transfer parameters from groundings to neural nets
        weightFactory = new WeightFactory();
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
