package networks.structure.building.debugging;

import grounding.GroundingSample;
import grounding.debugging.GroundingDebugger;
import networks.computation.training.NeuralSample;
import networks.computation.training.strategies.debugging.NeuralDebugging;
import pipelines.Pipeline;
import pipelines.debuging.End2EndDebugger;
import settings.Settings;
import settings.Sources;
import networks.computation.debugging.drawing.NeuralNetDrawer;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralDebugger extends End2EndDebugger<NeuralSample> implements NeuralDebugging {
    private static final Logger LOG = Logger.getLogger(NeuralDebugger.class.getName());

    public GroundingDebugger groundingDebugger;

    public NeuralDebugger(Settings settings) {
        super(settings);
        drawer = new NeuralNetDrawer(settings);
    }


    public NeuralDebugger(Sources sources, Settings settings) {
        super(sources, settings);
        drawer = new NeuralNetDrawer(settings);
    }

    @Override
    public void debug(NeuralSample neuralSample) {
        drawer.draw(neuralSample);
    }

    @Override
    public Pipeline<Sources, Stream<NeuralSample>> buildPipeline() {
        Pipeline<Sources, Stream<GroundingSample>> groundingPipeline = pipeline.registerStart(groundingDebugger.buildPipeline());
        if (intermediateDebug) {
            groundingDebugger.addDebugStream(groundingPipeline);
        }
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildNeuralNets =
                pipeline.registerEnd(end2endTrainigBuilder.buildNeuralNets(settings, groundingDebugger.getWeightFactory()));
        groundingPipeline.connectAfter(buildNeuralNets);
        return pipeline;
    }
}
