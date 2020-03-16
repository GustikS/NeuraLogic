package cz.cvut.fel.ida.neural.networks.structure.components;

import cz.cvut.fel.ida.algebra.functions.Activation;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;

import java.util.List;

/**
 * Created by gusta on 16.3.17.
 */
public class NeuralLayer {
    List<WeightedNeuron> neurons;
    Activation activation;
}
