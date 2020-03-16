package cz.cvut.fel.ida.neural.networks.structure.building;

import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.neural.networks.structure.building.builders.StatesBuilder;
import cz.cvut.fel.ida.neural.networks.structure.building.factories.NeuralNetFactory;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class NeuralBuilder {
    private static final Logger LOG = Logger.getLogger(NeuralBuilder.class.getName());
    Settings settings;

    public NeuralBuilder(Settings settings) {
        this.settings = settings;
        statesBuilder = new StatesBuilder(settings);
        weightFactory = new WeightFactory();
        neuronFactory = new NeuronFactory(weightFactory, settings);
        networkFactory = new NeuralNetFactory(settings);
    }

    public StatesBuilder statesBuilder;
    public NeuronFactory neuronFactory;
    public NeuralNetFactory networkFactory;

    public WeightFactory weightFactory;

    public void setFactoriesFrom(NeuralBuilder other) {
        this.neuronFactory = other.neuronFactory;
        this.networkFactory = other.networkFactory;
        this.weightFactory = other.weightFactory;
        this.statesBuilder = other.statesBuilder;
    }
}