package networks.structure.metadata.states;

import networks.computation.evaluation.functions.Activation;
import networks.computation.iteration.actions.StateVisitor;
import networks.structure.components.weights.Weight;

/**
 * Mainly annotation interfaces used for clarity of stateful processing. It is an interface (not abstract class) so that existing classes (e.g. Value) can be used directly as a state.
 * For other cases, special class States contains particular formations of useful states.
 */
public interface State {

    /**
     * Stateful values held by a Neuron for use during neural computation
     */
    interface Computation extends State {
        void invalidate();

        default boolean ready4activation(StateVisitor visitor) {
            return visitor.ready4activation(this);
        }

        default <V> V activateOutput(StateVisitor<V> visitor, Activation activation) {
            return visitor.activateOutput(this, activation);
        }

        default <V> V getOutput(StateVisitor<V> visitor) {
            return visitor.getOutput(this);
        }

        default <V> V getCumulation(StateVisitor<V> visitor) {
            return visitor.getCumulation(this);
        }

        default <V> void cumulate(StateVisitor<V> visitor, V from) {
            visitor.cumulate(from, this);
        }

        default <V> void cumulate(StateVisitor<V> visitor, V from, Weight weight) {
            visitor.cumulate(from, this, weight);
        }
    }

    /**
     * Structural changes to neurons held as a state corresponding to each Neuron in a neural network
     */
    interface Structure extends State {

    }
}