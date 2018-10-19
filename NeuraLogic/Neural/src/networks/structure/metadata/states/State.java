package networks.structure.metadata.states;

import networks.computation.iteration.actions.StateVisitor;

/**
 * Mainly annotation interfaces used for clarity of stateful processing. It is an interface (not abstract class) so that existing classes (e.g. Value) can be used directly as a state.
 * For other cases, special class States contains particular formations of useful states.
 */
public interface State {

    default <V> V accept(StateVisitor<V> visitor){
        return visitor.visit(this);
    }

    /**
     * Stateful values held by a Neuron for use during neural computation
     */
    interface Computation extends State {
        void invalidate();


    }

    /**
     * Structural changes to neurons held as a state corresponding to each Neuron in a neural network
     */
    interface Structure extends State {

    }
}