package networks.structure.components.types;

import networks.structure.components.NeuralLayer;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.states.State;

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
