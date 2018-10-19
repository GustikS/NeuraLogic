package networks.computation.iteration.actions;

import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public abstract class StateVisitor<V> {
    private static final Logger LOG = Logger.getLogger(StateVisitor.class.getName());

    /**
     * If multiple stateVisitors are visiting the same neuron's state at the same time, they need to have their own view
     * of the state. This is index of such a view, as given by index of the Thread encompassing this StateVisitor.
     */
    public int state_index;

    public abstract V visit(State state);

}