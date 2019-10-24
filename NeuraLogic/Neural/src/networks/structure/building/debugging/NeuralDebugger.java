package networks.structure.building.debugging;

import networks.computation.training.NeuralSample;
import settings.Settings;
import utils.PipelineDebugger;
import utils.drawing.NeuralNetDrawer;

import java.util.logging.Logger;

public class NeuralDebugger extends PipelineDebugger<NeuralSample> {
    private static final Logger LOG = Logger.getLogger(NeuralDebugger.class.getName());

    public NeuralDebugger(String[] args, Settings settings) {
        super(args, settings);
        drawer = new NeuralNetDrawer(settings);
    }

    @Override
    public void debug(NeuralSample neuralSample) {
        drawer.draw(neuralSample);
    }

}
