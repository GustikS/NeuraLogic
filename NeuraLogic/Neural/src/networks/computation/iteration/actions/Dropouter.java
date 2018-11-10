package networks.computation.iteration.actions;

import networks.computation.iteration.NeuronIterating;
import networks.computation.iteration.NeuronVisitorWeighted;
import networks.computation.iteration.Topologic;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.neurons.types.AtomNeuron;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.logging.Logger;

public class Dropouter implements NeuronVisitorWeighted {
    private static final Logger LOG = Logger.getLogger(Dropouter.class.getName());

    Settings settings;

    private Dropouter(Settings settings) {
        this.settings = settings;
    }

    /**
     * Return a suitable network iterator
     *
     * @param queryNeuron
     * @return
     */
    public static NeuronIterating getFor(Settings settings, QueryNeuron queryNeuron) {
        Dropouter dropouter = new Dropouter(settings);
        AtomNeuron<State.Computation> outputNeuron = queryNeuron.neuron;
        NeuralNetwork<State.Structure> network = queryNeuron.evidence;
        if (network instanceof TopologicNetwork) {
            new Topologic((TopologicNetwork) network, null).new TDownIterator(outputNeuron, dropouter);
        } else {
            //todo
        }
    }

    @Override
    public void propagate(Neuron neuron) {
        if (settings.random.nextDouble() < settings.dropoutRate)
            neuron.dropout = true;
    }

    @Override
    public void propagate(WeightedNeuron neuron) {
        propagate((Neuron) neuron);
    }
}
