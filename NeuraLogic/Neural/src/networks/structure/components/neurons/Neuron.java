package networks.structure.components.neurons;

import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.functions.Aggregation;
import networks.structure.metadata.states.State;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Neuron class is a pure container. The various logics of the various operations that may be performed upon neurons (and the particular subtypes)
 * are outsourced to NeuronVisitors (and StateVisitors for the state field). Putting the logic right into neuron classes might be slightly more efficient (=no visitors dispatch)
 * but it would introduce a messy combinatorial explosion of all the different modes of evaluation, backpropagation, dropout etc. in this single class.
 *
 * @param <T>
 * @param <S>
 */
public class Neuron<T extends Neurons, S extends State.Neural> implements Neurons {
    private static final Logger LOG = Logger.getLogger(Neuron.class.getName());
    /**
     * Globally unique index of creation of this neuron (=fast hash)
     */
    @NotNull
    public final int index;
    /**
     * Represents original logic of creation, typically unique across all the networks (in case of neuron sharing)
     */
    @Nullable
    public String id;
    /**
     * Stores (intermediate) values, or gradient, or both for this neuron for computation reuse. It has to be a separate class
     * since there is a wide variety of information that may be stored in a Neuron - e.g. it may also store (number of) parents
     * and all States may be stored in an array for minibatch access to this Neuron.
     * <p>
     * It's not nice, but neurons have to hold states - that is the most efficient choice, since this is the most efficient place to bind
     * the intermediate values of computation over neurons with the iteration over them. Not storing state directly in Neuron
     * would require some mapping (e.g. Hashmap or BST) between neuron and the corresponding values, which would be computationally inefficient to search within.
     * <p>
     * This is thus faster than searching in explicit maps (more correctly placed in Evaluator and/or Backprop),
     * but the states need to be treated more carefully (logic of creation taken by respective factories/builders).
     */
    @Nullable
    private final S state;
    /**
     * If not shared, an elementary State can be freely used to store any information (most efficient mode)
     */
    public boolean isShared;
    /**
     * Activation function - moved to ActivationState
     */
    @NotNull
    @Deprecated
    public final Activation activation;
    /**
     * We want fast iteration over inputs - todo test - consider array here with grounder storing the inputMappings in a list first
     */
    @Nullable   // because FactNeurons have no inputs (null check will be faster that isEmpty())
    protected ArrayList<T> inputs;
    /**
     * Depth of this neuron. Might be useful e.g. for Dropout or some transformations. todo
     */
    int layer;

    public Neuron(int index, String id, S state, Activation activation) {
        this.index = index;
        this.id = id;
        this.state = state;
        this.activation = activation;
        inputs = new ArrayList<>();
    }

    public void addInput(T input) {
        inputs.add(input);
    }

    @Override
    public final ArrayList<T> getInputs() {
        return inputs;
    }

    public void invalidate() {
        state.invalidate();
    }

    @Override
    @NotNull
    public Aggregation getAggregation() {
        return state.getAggregation();
    }

    public final boolean hasNoInputs() {
        return (inputs == null || inputs.isEmpty());
    }

    public int inputCount() {
        return inputs.size();
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Neuron obj1 = (Neuron) obj;
        return this.id.equals(obj1.getId());
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    /**
     * Get the Computation State of this Neuron.
     * The index is to tunnel through the ComputationStateComposite if multiple computation states exist in parallel.
     *
     * @param index
     * @return
     */
    @Contract(pure = true)
    public final State.Neural.Computation getComputationView(int index) {
        return state.getComputationView(index);
    }

    /**
     * Get the raw State of this Neuron.
     * @return
     */
    @Deprecated
    @Contract(pure = true)
    public final State.Neural getState() {
        return state;
    }
}