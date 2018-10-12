package networks.structure.networks;

import ida.utils.tuples.Pair;
import learning.Example;
import networks.structure.metadata.states.NeuronStates;
import networks.structure.metadata.states.State;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.WeightedNeuron;
import networks.structure.weights.Weight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * A neural network is a container to store context information for neurons, that may change from network to network.
 * Particularly this is neuronStates structure that holds inputs of shared neurons that vary across different networks.
 *
 * Created by gusta on 8.3.17.
 * <p>
 * //todo after creation and post-processing, add a transformation to a more optimized version (everything based on int, maybe even precompute layers, remove recursion)
 */
public abstract class NeuralNetwork<N extends State.Structure> implements Example {
    private static final Logger LOG = Logger.getLogger(NeuralNetwork.class.getName());
    /**
     * Should be as unique as possible
     */
    @NotNull
    String id;

    /**
     * A structure to store States and Search for neurons within this network (if available)
     */
    @Nullable
    public
    NeuronStates<N> neuronStates;

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

    public abstract <T extends WeightedNeuron, S extends State.Computation> Iterator<Pair<T, Weight>> getInputs(WeightedNeuron<T, S> neuron);

    public abstract <T extends Neuron, S extends State.Computation> Iterator<T> getInputs(Neuron<T, S> neuron);

    public abstract <T extends Neuron, S extends State.Computation> Iterator<T> getOutputs(Neuron<T, S> neuron);

}