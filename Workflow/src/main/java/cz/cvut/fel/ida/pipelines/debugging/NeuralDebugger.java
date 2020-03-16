package cz.cvut.fel.ida.pipelines.debugging;

import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.pipelines.debugging.drawing.NeuralNetDrawer;

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
