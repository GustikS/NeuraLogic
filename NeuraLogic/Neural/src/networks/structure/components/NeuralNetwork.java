package networks.structure.components;

import ida.utils.tuples.Pair;
import learning.Example;
import networks.computation.iteration.DFSstack;
import networks.computation.iteration.NeuronIterating;
import networks.computation.iteration.NeuronVisiting;
import networks.structure.metadata.states.State;
import networks.structure.metadata.states.StatesCache;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * A neural network is a container to store context information for neurons, that may change from network to network.
 * Particularly this is neuronStates structure that holds inputs of shared neurons that vary across different networks.
 * <p>
 * Created by gusta on 8.3.17.
 * <p>
 * //todo after creation and post-processing, add a transformation to a more optimized version (everything based on int, maybe even precompute layers, remove recursion)
 */
public abstract class NeuralNetwork<N extends State.Neural.Structure> implements Example {
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
    public StatesCache<N> neuronStates;

    /**
     * If there are no shared neurons (or no parallel access to them), there is no need to store extra states of them
     */
    boolean hasSharedNeurons;

    /**
     * A subset of all weights from a template that are used within this network.
     * todo NOT INDEXABLE! remove? yes remove...
     */
    @NotNull
    @Deprecated
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

    public abstract <T extends Neuron, S extends State.Neural> Pair<Iterator<T>, Iterator<Weight>> getInputs(WeightedNeuron<T, S> neuron);

    /**
     * todo FactNeurons should return empty inputs
     *
     * @param neuron
     * @param <T>
     * @param <S>
     * @return
     */
    public abstract <T extends Neuron, S extends State.Neural> Iterator<T> getInputs(Neuron<T, S> neuron);

    public abstract <T extends Neuron, S extends State.Neural> Iterator<T> getOutputs(Neuron<T, S> neuron);

    /**
     * Bind this network representation with the preferred choice of iteration over neurons in it.
     * E.g. if no neurons are stored in this network, go with simple DFS (default), but
     * if neurons are stored in topological order (TopologicNetwork) iterate efficiently in linear fashion.
     *
     * @param vNeuronVisitor
     * @return
     */
    @Deprecated
    public <V> NeuronIterating getPreferredBUpIterator(NeuronVisiting<V> vNeuronVisitor, Neuron<Neuron, State.Neural> outputNeuron) {
        return new DFSstack().new BottomUp<>(vNeuronVisitor, this, outputNeuron);
    }

    @Deprecated
    public <V> NeuronIterating getPreferredTDownIterator(NeuronVisiting<V> vNeuronVisitor, Neuron<Neuron, State.Neural> outputNeuron) {
        return new DFSstack().new TopDown<>(vNeuronVisitor, this, outputNeuron);
    }

}