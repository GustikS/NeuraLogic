package networks.structure.factories;

import constructs.building.factories.WeightFactory;
import networks.structure.networks.State;
import settings.Settings;

import java.util.logging.Logger;

public class NeuralBuilder {
    private static final Logger LOG = Logger.getLogger(NeuralBuilder.class.getName());
    Settings settings;

    public NeuralBuilder(Settings settings) {
        this.settings = settings;
    }

    public NeuronFactory neuronFactory;
    public WeightFactory weightFactory;
    public NetworkFactory networkFactory;

    public void setFactoriesFrom(NeuralBuilder other) {
        this.neuronFactory = other.neuronFactory;
        this.networkFactory = other.networkFactory;
        this.weightFactory = other.weightFactory;
    }

    State.Structure getStructureState() {

    }
}