package networks.structure.components.neurons;

import networks.structure.components.weights.Weight;

import java.util.logging.Logger;

/**
 * This representation would be nice but uses unnecessary extra memory. Splitting representation of a Neuron into WeightedNeuron instead.
 * @param <T>
 */
@Deprecated
public class InputEdge<T extends Neurons> {
    private static final Logger LOG = Logger.getLogger(InputEdge.class.getName());

    T input;


    public class Weighted extends InputEdge<T> {

        Weight weight;


    }
}