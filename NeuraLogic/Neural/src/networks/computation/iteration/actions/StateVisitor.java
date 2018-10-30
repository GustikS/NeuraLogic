package networks.computation.iteration.actions;

import networks.computation.evaluation.functions.Activation;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public abstract class StateVisitor<V> {
    private static final Logger LOG = Logger.getLogger(StateVisitor.class.getName());

    /**
     * If multiple stateVisitors are visiting the same neuron's state at the same time, they need to have their own view
     * of the state. This is index of such a view, as given by index of the Thread encompassing this StateVisitor.
     */
    public int stateIndex;

    public StateVisitor() {
        stateIndex = -1;
    }

    public StateVisitor(int stateIndex) {
        this.stateIndex = stateIndex;
    }

    /**
     * Check, typically using a State of the Neuron, whether this neuron is ready to propagate its result
     *
     * @param state
     * @return
     */
    public abstract boolean ready4activation(State.Computation state);

    /**
     * Transformation of the State of this neuron through its activation function into a result V (typically a Value)
     * todo consider void return type for speedup if not used
     *
     * @param activation
     * @param state
     * @return
     */
    public abstract V activateOutput(State.Computation activation, Activation state);

    /**
     * Get particular Value from this state w.r.t. this type of state visitor (e.g. either outputValue or Gradient)
     *
     * @param state
     * @return
     */
    public abstract V getOutput(State.Computation state);

    /**
     * Get the V (typically Value) acummulated from neighbours during iteration.
     *
     * @param state
     * @return
     */
    public abstract V getCumulation(State.Computation state);

    /**
     * Propagate result from the neuron "from" into the destination neuron
     *
     * @param from
     * @param to
     */
    public abstract void cumulate(V from, State.Computation to);

    /**
     * Propagate result from the neuron "from" into the destination neuron using the information from the weight
     *
     * @param from
     * @param to
     */
    public abstract void cumulate(V from, State.Computation to, Weight weight);

}