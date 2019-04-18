package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.types.AtomNeuron;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.metadata.states.State;

/**
 * Created by gusta on 14.3.17.
 */
public class IsoValueNetworkCompressor implements NetworkReducing, NetworkMerging {
    @Override
    public NeuralNetwork merge(NeuralNetwork a, NeuralNetwork b) {
        return null;
    }

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Structure> inet, AtomNeuron<State.Neural> outputStart) {
        return null;
    }
}
