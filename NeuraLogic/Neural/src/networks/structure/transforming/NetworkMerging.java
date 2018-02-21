package networks.structure.transforming;

import networks.structure.NeuralNetwork;

/**
 * Created by gusta on 14.3.17.
 */
public interface NetworkMerging {
    NeuralNetwork merge(NeuralNetwork a, NeuralNetwork b);
}
