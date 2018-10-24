package networks.computation.training;

import networks.computation.iteration.TopDown;
import networks.computation.iteration.actions.Backproper;
import networks.computation.iteration.actions.Evaluator;
import networks.computation.training.evaluation.values.Value;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.logging.Logger;

public class Backpropagation {
    private static final Logger LOG = Logger.getLogger(Backpropagation.class.getName());
    private final Settings settings;

    Backproper backproper;

    public Backpropagation(Settings settings){
        this.settings = settings;
    }

    public static TopDown<Value> getTopDownPropagator(Settings settings, NeuralNetwork<State.Structure> network, Neuron<Neuron, State.Computation> outputNeuron, Backproper backproper){

    }
}
