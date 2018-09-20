package networks.structure;

import networks.evaluation.functions.Activation;

import java.util.List;

/**
 * Created by gusta on 16.3.17.
 */
public class NeuralLayer {
    List<WeightedNeuron> neurons;
    Activation activation;
}
