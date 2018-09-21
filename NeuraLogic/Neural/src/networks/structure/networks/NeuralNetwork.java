package networks.structure.networks;

import ida.utils.tuples.Pair;
import learning.Example;
import networks.evaluation.iteration.State;
import networks.structure.metadata.NeuronMapping;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.WeightedNeuron;
import networks.structure.weights.Weight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Created by gusta on 8.3.17.
 * <p>
 * //todo after creation and post-processing, add a transformation to a more optimized version (everything based on int, maybe even precompute layers, remove recursion)
 */
public abstract class NeuralNetwork<N extends State> implements Example {
    String id;

    //todo next
    boolean BST = true;

    /**
     * A subset of all weights from a template that are used within this network.
     */
    @NotNull
    Weight[] activeWeights;

    /**
     * Used for computational efficiency needs. Indexable by findNeuron().
     * Depending on different settings of neuron sharing, Evaluation, Backprop and Parallelization, the network context needs to store information that changes from network to network.
     * These are all/either of neuron values, gradients, #parents
     */
    @Nullable
    N[] neuronStates;

    /**
     * A stored mapping of all contained neurons to internal index (for a more efficient storing and access to neurons, if needed, than holding them within na HashMap)
     */
    @Nullable
    private int[] sortedNeuronIndices;

    /**
     * For topologicNetwork must be aligned (ordered the same as) the topologic neurons list - saves one findNeuron call.
     * For other networks without implicit neurons ordering, index of a neuron must be found first.
     */
    @Nullable
    NeuronMapping[] inputOvermap;

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

    public abstract <T extends WeightedNeuron, S extends State> Iterator<Pair<T, Weight>> getInputs(WeightedNeuron<T, S> neuron);

    public abstract <T extends Neuron, S extends State> Iterator<T> getInputs(Neuron<T, S> neuron);

    public abstract <T extends Neuron, S extends State> Iterator<T> getOutputs(Neuron<T, S> neuron);

    public void setBST(int limit) {
        if (getSize() < limit)
            BST = true;
        else
            BST = false;
    }

    N getState(Neuron neuron) {
        return neuronStates[findNeuron(neuron.index)];
    }

    NeuronMapping getInputOverMapping(Neuron neuron){
        return inputOvermap[findNeuron(neuron.index)];
    }

    public int findNeuron(int neuronIndex) {
        if (BST)
        //todo binary search
                else
        //use hashmap

    }

}