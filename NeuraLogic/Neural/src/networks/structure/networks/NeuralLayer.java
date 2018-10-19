package networks.structure.networks;

import networks.computation.functions.Activation;
import networks.structure.neurons.WeightedNeuron;

import java.util.List;

/**
 * Created by gusta on 16.3.17.
 */
public class NeuralLayer {
    List<WeightedNeuron> neurons;
    Activation activation;
}
