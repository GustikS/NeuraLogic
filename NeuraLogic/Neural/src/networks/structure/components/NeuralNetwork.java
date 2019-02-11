package networks.structure.components;

import ida.utils.tuples.Pair;
import learning.Example;
import networks.computation.iteration.NeuronIterating;
import networks.computation.iteration.modes.DFSstack;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;
import networks.structure.metadata.states.StatesCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * A neural network is a container to store context information for neurons, that may change from network to network.
 * Particularly this is neuronStates structure that holds inputs of shared neurons that vary across different networks.
 * <p>
 * Created by gusta on 8.3.17.
 * <p>
 */
public class NeuralNetwork<N extends State.Neural.Structure> implements Example {
    private static final Logger LOG = Logger.getLogger(NeuralNetwork.class.getName());
    /**
     * Should be as unique as possible
     */
    @NotNull
    String id;

    /**
     * Number of neurons
     */
    int size;

    /**
     * If there are no shared neurons (or no parallel access to them), there is no need to store extra states of them
     */
    boolean hasSharedNeurons;

    /**
     * Whether there are neurons which do not propagate values into all the inputs (e.g. max-pooling).
     */
    public boolean containsPooling;

    /**
     * A structure to store States and Search for neurons within this network (if available)
     */
    @Nullable
    public StatesCache<N> neuronStates;


    public NeuralNetwork() {
    }

    public NeuralNetwork(String id, int size) {
        this.id = id;
        this.size = size;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    public void setId(String id) {
        this.id = id;
    }

    public N getState(Neuron neuron) {
        if (neuronStates != null) {
            return neuronStates.getState(neuron);
        } else
            return null;
    }


    /**
     * Returning pair of iterators should be faster than returning iterator of pairs, which would need to actually create the pair object every time during iteration
     *
     * @param neuron
     * @param <T>
     * @param <S>
     * @return
     */
    public <T extends Neuron, S extends State.Neural> Pair<Iterator<T>, Iterator<Weight>> getInputs(WeightedNeuron<T, S> neuron) {
        if (!hasSharedNeurons)
            return new Pair<>(neuron.getInputs().iterator(), neuron.getWeights().iterator());
        else if (neuronStates != null) {
            neuronStates.
        }
    }

    public <T extends Neuron, S extends State.Neural> Pair<Iterator<T>, Iterator<Weight>> getInputs(WeightedNeuron<T, S> neuron, int[] inputMask) {
        ArrayList<T> inputs = neuron.getInputs();
        ArrayList<Weight> weights = neuron.getWeights();

        ArrayList<T> maskedInputs = new ArrayList<>(inputMask.length);
        ArrayList<Weight> maskedWeights = new ArrayList<>(inputMask.length);

        for (int i = 0; i < inputMask.length; i++) {
            int i1 = inputMask[i];
            maskedInputs.add(inputs.get(i1));
            maskedWeights.add(weights.get(i1));
        }
        return new Pair<>(maskedInputs.iterator(), maskedWeights.iterator());
    }

    public <T extends Neuron, S extends State.Neural> Iterator<T> getInputs(Neuron<T, S> neuron, int[] inputMask) {
        ArrayList<T> inputs = neuron.getInputs();
        ArrayList<T> maskedInputs = new ArrayList<>(inputMask.length);

        for (int i = 0; i < inputMask.length; i++) {
            int i1 = inputMask[i];
            maskedInputs.add(inputs.get(i1));
        }
        return maskedInputs.iterator();
    }

    public <T extends Neuron, S extends State.Neural> Iterator<T> getInputs(Neuron<T, S> neuron) {
        return neuron.getInputs().iterator();
    }

    public <T extends Neuron, S extends State.Neural> Iterator<T> getOutputs(Neuron<T, S> neuron) {
        return null;
    }

    /**
     * Bind this network representation with the preferred choice of iteration over neurons in it.
     * E.g. if no neurons are stored in this network, go with simple DFS (default), but
     * if neurons are stored in topological order (TopologicNetwork) iterate efficiently in linear fashion.
     *
     * @param vNeuronVisitor
     * @return
     */
    @Deprecated
    public <V> NeuronIterating getPreferredBUpIterator(NeuronVisitor.Weighted vNeuronVisitor, Neuron<Neuron, State.Neural> outputNeuron) {
        return new DFSstack().new BUpIterator((NeuralNetwork<State.Neural.Structure>) this, outputNeuron, vNeuronVisitor);
    }

    @Deprecated
    public <V> NeuronIterating getPreferredTDownIterator(NeuronVisitor.Weighted vNeuronVisitor, Neuron<Neuron, State.Neural> outputNeuron) {
        return new DFSstack().new TDownIterator((NeuralNetwork<State.Neural.Structure>) this, outputNeuron, vNeuronVisitor);
    }

}