package cz.cvut.fel.ida.pipelines.debugging;

import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.debugging.drawing.NeuralNetDrawer;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralDebugger extends End2EndDebugger<NeuralSample> implements NeuralDebugging {
    private static final Logger LOG = Logger.getLogger(NeuralDebugger.class.getName());

    public NeuralDebugger(Settings settings) {
        super(settings);
        if (settings.drawing)
            drawer = new NeuralNetDrawer(settings);
    }

    public NeuralDebugger(Sources sources, Settings settings) {
        super(sources, settings);
        if (settings.drawing)
            drawer = new NeuralNetDrawer(settings);
    }

    @Override
    public void debug(NeuralSample neuralSample) {
        LOG.fine(neuralSample.toString());
        if (settings.drawing)
            drawer.draw(neuralSample);
    }

    @Override
    public Pipeline<Sources, Stream<NeuralSample>> buildPipeline() {
        Pipeline<Sources, Pair<Template, Stream<LogicSample>>> sourcesPipeline = pipeline.registerStart(this.end2endTrainigBuilder.buildFromSources(sources, settings));
        //to transfer parameters from groundings to neural nets
        WeightFactory weightFactory = new WeightFactory();

        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(this.end2endTrainigBuilder.buildGrounding(settings, weightFactory));
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralPipeline = pipeline.registerEnd(this.end2endTrainigBuilder.buildNeuralNets(settings, weightFactory));

        sourcesPipeline.connectAfter(groundingPipeline);
        groundingPipeline.connectAfter(neuralPipeline);

        return pipeline;
    }
}
