package networks.computation.iteration.visitors.states;

import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * todo test version with separate visitors for each method for performance - if fast enough, switch to it
 * @param <V>
 */
public abstract class StateVisiting<V> {
    private static final Logger LOG = Logger.getLogger(StateVisiting.class.getName());

    /**
     * If multiple stateVisitors are visiting the same neuron's state at the same time, they need to have their own view
     * of the state. This is index of such a view, as given by index of the Thread encompassing this StateVisitor.
     */
    public int stateIndex;

    private StateVisiting() {
        stateIndex = -1;
    }

    public StateVisiting(int stateIndex) {
        this.stateIndex = stateIndex;
    }

    public abstract V visit(State<V> state);

    public abstract static class Computation extends StateVisiting<Value> {

        public Computation(int stateIndex) {
            super(stateIndex);
        }

        @Override
        public Value visit(State<Value> state) {
            LOG.severe("ComputationVisitor called for generic State<Value>.");
            return state.accept(this);
        }

        /**
         * Transformation of the State of this neuron through its activation function into a result Value
         *
         * @param state
         * @return
         */
        public abstract Value visit(State.Neural.Computation state);
    }
}