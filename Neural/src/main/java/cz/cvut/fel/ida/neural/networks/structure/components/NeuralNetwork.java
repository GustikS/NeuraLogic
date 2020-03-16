package cz.cvut.fel.ida.neural.networks.structure.components;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.learning.Example;
import cz.cvut.fel.ida.neural.networks.computation.iteration.NeuronIterating;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSstack;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.networks.InputsGetter;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.networks.OutputsGetter;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.StatesCache;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.NeuronMapping;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.WeightedNeuronMapping;

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
    protected
    String id;

    /**
     * Number of neurons
     */
    protected int neuronCount;

    /**
     * todo
     */
    int edgeCount;

    /**
     * If there are no shared neurons (or no parallel access to them), there is no need to store extra states of them
     */
    public boolean hasSharedNeurons;

    /**
     * Whether there are neurons which do not propagate values into all the inputs (e.g. max-pooling).
     */
    public boolean containsInputMasking; //todo test template with Max aggregation

    /**
     * Whether there are neurons which propagate values to inputs in non-standard manner (requires special visitors)
     */
    public boolean containsCrossProducts;

    /**
     * A structure to store States and Search for neurons within this network (if available)
     */
    @Nullable
    public StatesCache<N> neuronStates; //todo use this

    InputsGetter inputsGetter;
    InputsGetter.Weighted weightedInputsGetter;
    OutputsGetter outputsGetter;

    public NeuralNetwork(String id, int size) {
        this.id = id;
        this.neuronCount = size;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getNeuronCount() {
        return neuronCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public N getState(Neurons neuron) {
        if (neuronStates != null) {
            return neuronStates.getState(neuron);
        } else
            return null;
    }

    /**
     * Load/process all information from the Structural states of neurons
     */
    public void initializeStatesCache(int stateView) {
        if (neuronStates != null) {
            neuronStates.initialize(stateView);
        }
    }


    /**
     * Returning pair of iterators should be faster than returning iterator of pairs, which would need to actually create the pair object every time during iteration
     *
     * @param neuron
     * @param <T>
     * @param <S>
     * @return
     */
    public <T extends Neurons, S extends State.Neural> Pair<Iterator<T>, Iterator<Weight>> getInputs(WeightedNeuron<T, S> neuron) {
        if (neuron.isShared) {
            State.Structure neuralState = getState(neuron);
            WeightedNeuronMapping<T> visit = weightedInputsGetter.visit(neuralState);
            Iterator<T> iterator = visit.iterator();
            Iterator<Weight> weightIterator = visit.weightIterator();
            return new Pair<>(iterator, weightIterator);
        } else
            return new Pair<>(neuron.getInputs().iterator(), neuron.getWeights().iterator());
    }

    public <T extends Neurons, S extends State.Neural> Pair<Iterator<T>, Iterator<Weight>> getInputs(WeightedNeuron<T, S> neuron, int[] inputMask) {
        ArrayList<T> inputs;
        ArrayList<Weight> weights;

        if (neuron.isShared) {
            State.Structure neuralState = getState(neuron);
            WeightedNeuronMapping<T> visit = weightedInputsGetter.visit(neuralState);
            Iterator<T> iterator = visit.iterator();
            Iterator<Weight> weightIterator = visit.weightIterator();
            inputs = new ArrayList<>();
            weights = new ArrayList<>();
            iterator.forEachRemaining(inputs::add);
            weightIterator.forEachRemaining(weights::add);
        } else {
            inputs = neuron.getInputs();
            weights = neuron.getWeights();
        }

        ArrayList<T> maskedInputs = new ArrayList<>(inputMask.length);
        ArrayList<Weight> maskedWeights = new ArrayList<>(inputMask.length);

        for (int i = 0; i < inputMask.length; i++) {
            int i1 = inputMask[i];
            maskedInputs.add(inputs.get(i1));
            maskedWeights.add(weights.get(i1));
        }
        return new Pair<>(maskedInputs.iterator(), maskedWeights.iterator());
    }

    public <T extends Neurons, S extends State.Neural> Iterator<T> getInputs(BaseNeuron<T, S> neuron, int[] inputMask) {
        ArrayList<T> inputs;

        if (neuron.isShared) {
            State.Structure neuralState = getState(neuron);
            NeuronMapping<T> visit = inputsGetter.visit(neuralState);
            inputs = new ArrayList<>();
            visit.iterator().forEachRemaining(inputs::add);
        } else {
            inputs = neuron.getInputs();
        }
        ArrayList<T> maskedInputs = new ArrayList<>(inputMask.length);

        for (int i = 0; i < inputMask.length; i++) {
            int i1 = inputMask[i];
            maskedInputs.add(inputs.get(i1));
        }
        return maskedInputs.iterator();
    }

    public <T extends Neurons, S extends State.Neural> Iterator<T> getInputs(Neurons<T, S> neuron) {
        if (neuron.isShared()) {
            State.Structure neuralState = getState(neuron);
            NeuronMapping<T> visit = inputsGetter.visit(neuralState);
            return visit.iterator();
        } else
            return neuron.getInputs().iterator();
    }

    public <T extends Neurons, S extends State.Neural> Iterator<T> getInputs(BaseNeuron<T, S> neuron) {
        if (neuron.isShared) {
            State.Structure neuralState = getState(neuron);
            NeuronMapping<T> visit = inputsGetter.visit(neuralState);
            return visit.iterator();
        } else
            return neuron.getInputs().iterator();
    }

    public <T extends Neurons, S extends State.Neural> Iterator<Neurons> getOutputs(BaseNeuron<T, S> neuron) {
        State.Structure neuralState = getState(neuron);
        NeuronMapping<Neurons> visit = outputsGetter.visit(neuralState);
        return visit.iterator();
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
    public <V> NeuronIterating getPreferredBUpIterator(NeuronVisitor.Weighted vNeuronVisitor, BaseNeuron<Neurons, State.Neural> outputNeuron) {
        return new DFSstack().new BUpIterator((NeuralNetwork<State.Neural.Structure>) this, outputNeuron, vNeuronVisitor);
    }

    @Deprecated
    public <V> NeuronIterating getPreferredTDownIterator(NeuronVisitor.Weighted vNeuronVisitor, BaseNeuron<Neurons, State.Neural> outputNeuron) {
        return new DFSstack().new TDownIterator((NeuralNetwork<State.Neural.Structure>) this, outputNeuron, vNeuronVisitor);
    }

    @Override
    public String toString() {
        return "net:" + id + ", neurons: " + neuronCount;
    }
}