package networks.structure.neurons;

import networks.evaluation.functions.Activation;
import networks.structure.metadata.states.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Neuron<T extends Neurons, S extends State.Computation> implements Neurons {
    private static final Logger LOG = Logger.getLogger(Neuron.class.getName());
    /**
     * Globally unique index of creation of this neuron (=hash)
     */
    public final int index;
    /**
     * Typically unique across all the networks in case of neuron sharing (represents logic of creation)
     */
    @NotNull
    public String id;
    /**
     * Stores value, or gradient, or both for this neuron for computation reuse (faster than explicit maps in Evaluator and/or Backprop, but cannot be used in parallel batch mode with various neuron sharings)
     */
    @Nullable
    private final S state;
    /**
     * If not shared, the state can be freely used to store information (most efficient mode)
     */
    public boolean isShared;
    /**
     * Activation function
     */
    @NotNull
    private final Activation activation;
    /**
     * We want fast iteration over inputs - todo test - consider array here with grounder storing the inputMappings in a list first
     */
    @Nullable   // because FactNeurons have no inputs (null check will be faster that isEmpty())
    protected ArrayList<T> inputs;

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

    public final boolean hasInputs() {
        return !inputs.isEmpty();
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
        WeightedNeuron obj1 = (WeightedNeuron) obj;
        return this.id.equals(obj1.getId());
    }

    @NotNull
    @Override
    public Activation getActivation() {
        return activation;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }
}