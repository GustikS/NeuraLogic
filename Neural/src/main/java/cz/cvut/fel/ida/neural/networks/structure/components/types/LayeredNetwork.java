package cz.cvut.fel.ida.neural.networks.structure.components.types;

import cz.cvut.fel.ida.neural.networks.structure.components.NeuralLayer;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.List;

/**
 * Created by gusta on 17.3.17.
 */
public class LayeredNetwork extends NeuralNetwork<State.Neural.Structure> {
    List<NeuralLayer> layers;

    public LayeredNetwork(String id, int size) {
        super(id, size);
    }
}
