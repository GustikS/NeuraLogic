package networks.structure;

import ida.utils.tuples.Pair;
import learning.Example;

import java.util.Iterator;

/**
 * Created by gusta on 8.3.17.
 * <p>
 * //todo after creation and post-processing, add a transformation to a more optimized version (everything based on int, maybe even precompute layers, remove recursion)
 */
public abstract class NeuralNetwork implements Example {
    String id;

    public NeuralNetwork() {
    }

    public NeuralNetwork(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract <T extends Neuron> Iterator<Pair<T, Weight>> getInputs(Neuron<T> neuron);
    public abstract <T extends Neuron> Iterator<Pair<T, Weight>> getOutputs(Neuron<T> neuron);


}