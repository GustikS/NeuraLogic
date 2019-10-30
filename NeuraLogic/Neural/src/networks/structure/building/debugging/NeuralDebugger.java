package networks.structure.building.debugging;

import grounding.GroundingSample;
import grounding.debugging.GroundingDebugger;
import networks.computation.training.NeuralSample;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;
import utils.PipelineDebugger;
import utils.drawing.NeuralNetDrawer;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralDebugger extends PipelineDebugger<NeuralSample> {
    private static final Logger LOG = Logger.getLogger(NeuralDebugger.class.getName());

    GroundingDebugger groundingDebugger;

    public NeuralDebugger(String[] args, Settings settings) {
        super(args, settings);
        drawer = new NeuralNetDrawer(settings);
        groundingDebugger = new GroundingDebugger(args, settings);
    }

    @Override
    public void debug(NeuralSample neuralSample) {
        drawer.draw(neuralSample);
    }

    @Override
    public Pipeline<Sources, Stream<NeuralSample>> buildPipeline() {
        Pipeline<Sources, Stream<GroundingSample>> groundingPipeline = pipeline.registerStart(groundingDebugger.buildPipeline());
        if (intermediateDebug){
            groundingDebugger.addDebug(groundingPipeline);
        }
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildNeuralNets =
                pipeline.registerEnd(end2endTrainigBuilder.buildNeuralNets(settings, groundingDebugger.getWeightFactory()));
        groundingPipeline.connectAfter(buildNeuralNets);
        return pipeline;
    }
}
