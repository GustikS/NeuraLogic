package networks.structure.networks;

import ida.utils.tuples.Pair;
import learning.Example;
import networks.evaluation.iteration.State;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.WeightedNeuron;
import networks.structure.weights.Weight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import settings.Settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 * <p>
 * //todo after creation and post-processing, add a transformation to a more optimized version (everything based on int, maybe even precompute layers, remove recursion)
 */
public abstract class NeuralNetwork<N extends State.Structure> implements Example {
    private static final Logger LOG = Logger.getLogger(NeuralNetwork.class.getName());
    String id;

    Settings.NeuronSearch neuronSearch;

    /**
     * If there are no shared neurons (or no parallel access to them), there is no need to store extra states of them
     */
    boolean hasSharedNeurons;

    /**
     * A subset of all weights from a template that are used within this network.
     * todo NOT INDEXABLE! remove?
     */
    @NotNull
    Weight[] activeWeights;

    /**
     * For topologicNetwork must be aligned (ordered the same as) the topologic neurons list - saves one findNeuron() call.
     * For other networks without implicit neurons ordering, index of a neuron must be found first with findNeuron against sortedNeuronIndices.
     * <p>
     * Depending on different settings of neuron sharing, Evaluation, Backprop and Parallelization, the network context needs to store information that changes from network to network.
     * For each neuron, these are all/either of inputs, outputs and number of parents. There is no need to store computation state at the level of network, as that belongs to individual neurons.
     */
    @Nullable
    N[] neuronStates;

    /**
     * A stored mapping of all contained neurons to internal index (for a more efficient storing and access to neurons, if needed, than holding them within na HashMap)
     */
    @Nullable
    private int[] heapSortedNeuronIndices;
    /**
     * The same mapping as above, but using a hashmap - may be faster for very big networks.
     */
    @Nullable
    private HashMap<Integer, Integer> neuronIndices;

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

    N getState(Neuron neuron) {
        int idx = findNeuron(neuron.index);
        if (idx < 0 || idx > neuronStates.length) {
            LOG.severe("ERROR - out of bounds access to getState of a neuron: " + neuron);
            return null;
        }
        return neuronStates[idx];
    }

    public int findNeuron(int neuronIndex) {
        switch (neuronSearch) {
            case LINEAR:
                return linear(neuronIndex);
            case BST:
                return bst(neuronIndex);
            case HASHMAP:
                return neuronIndices.get(neuronIndex);
        }
    }

    private int linear(int index) {
        for (int i = 0; i < heapSortedNeuronIndices.length; i++) {
            if (heapSortedNeuronIndices[i] == index)
                return i;
        }
        return -1;
    }

    private int bst(int target) {
        int index = 0;
        while (index < heapSortedNeuronIndices.length)
            if (target >= heapSortedNeuronIndices[index]) {
                index <<= 2;
            } else if (target < heapSortedNeuronIndices[index]) {
                index <<= 2 + 1;
            } else {
                return index;
            }
        return -1;
    }

}