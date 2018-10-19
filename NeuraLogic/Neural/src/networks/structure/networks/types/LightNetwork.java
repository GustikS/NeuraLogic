package networks.structure.networks.types;

import ida.utils.tuples.Pair;
import networks.structure.metadata.states.State;
import networks.structure.networks.NeuralNetwork;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.WeightedNeuron;
import networks.structure.weights.Weight;

import java.util.Iterator;
import java.util.logging.Logger;

public class LightNetwork<N extends State.Structure> extends NeuralNetwork<N> {
    private static final Logger LOG = Logger.getLogger(LightNetwork.class.getName());

    @Override
    public <T extends WeightedNeuron, S extends State.Computation> Iterator<Pair<T, Weight>> getInputs(WeightedNeuron<T, S> neuron) {
        return null;
    }

    @Override
    public <T extends Neuron, S extends State.Computation> Iterator<T> getInputs(Neuron<T, S> neuron) {
        return null;
    }

    @Override
    public <T extends Neuron, S extends State.Computation> Iterator<T> getOutputs(Neuron<T, S> neuron) {
        return null;
    }

    @Override
    public Integer getSize() {
        return null;
    }
}
