package networks.structure.components.neurons;

import networks.computation.evaluation.functions.Activation;
import networks.structure.metadata.states.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Neuron<T extends Neurons, S extends State.Computation> implements Neurons {
    private static final Logger LOG = Logger.getLogger(Neuron.class.getName());
    /**
     * Globally unique index of creation of this neuron (=fast hash)
     */
    @NotNull
    public final int index;
    /**
     * Represents original logic of creation, typically unique across all the networks in case of neuron sharing
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
    public final S state;
    /**
     * If not shared, the state can be freely used to store information (most efficient mode)
     */
    public boolean isShared;
    /**
     * Activation function
     */
    @NotNull
    public final Activation activation;
    /**
     * We want fast iteration over inputs - todo test - consider array here with grounder storing the inputMappings in a list first
     */
    @Nullable   // because FactNeurons have no inputs (null check will be faster that isEmpty())
    protected ArrayList<T> inputs;

    /**
     * It should be in State, but it is easier to have dropout here to be checked by a NeuronVisitor instead of StateVisitor
     * (since it is independent of the other values in State and might operate also with Neuron's inputs).
     */
    public boolean dropout;

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
    public Activation getActivation() {
        return activation;
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
}