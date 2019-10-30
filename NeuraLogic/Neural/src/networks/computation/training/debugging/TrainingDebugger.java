package networks.computation.training.debugging;

import networks.computation.training.NeuralSample;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;
import utils.PipelineDebugger;
import utils.drawing.NeuralNetDrawer;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingDebugger extends PipelineDebugger<NeuralSample> {
    private static final Logger LOG = Logger.getLogger(TrainingDebugger.class.getName());

    public TrainingDebugger(String[] args, Settings settings) {
        super(args);
        drawer = new NeuralNetDrawer(settings);
    }


    @Override
    public void debug(NeuralSample neuralSample) {
        drawer.draw(neuralSample);
    }

    @Override
    public Pipeline<Sources, Stream<NeuralSample>> buildPipeline() {
        return null;
    }
}