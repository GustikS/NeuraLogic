package networks.structure;

import networks.evaluation.values.Value;
import networks.structure.neurons.Neurons;

import java.util.logging.Logger;

/**
 * This representation would be nice but uses unnecessary extra memory. Splitting representation of a Neuron into WeightedNeuron instead.
 * @param <T>
 */
@Deprecated
public class InputEdge<T extends Neurons> {
    private static final Logger LOG = Logger.getLogger(InputEdge.class.getName());

    T input;

    public Value getInputValue(){
        return input.evaluate();
    }

    public class Weighted extends InputEdge<T> {

        Weight weight;

        public Value getInputValue(){
            return weight.value.multiplyBy(input.evaluate());
        }

    }
}