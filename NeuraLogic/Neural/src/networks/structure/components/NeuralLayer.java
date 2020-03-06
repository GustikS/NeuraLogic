package networks.structure.components;

import evaluation.functions.Activation;
import networks.structure.components.neurons.WeightedNeuron;

import java.util.List;

/**
 * Created by gusta on 16.3.17.
 */
public class NeuralLayer {
    List<WeightedNeuron> neurons;
    Activation activation;
}
