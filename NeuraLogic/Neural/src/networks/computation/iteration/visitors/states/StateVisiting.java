package networks.computation.iteration.visitors.states;

import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * @param <V>
 */
public abstract class StateVisiting<V> {
    private static final Logger LOG = Logger.getLogger(StateVisiting.class.getName());

    public abstract V visit(State<V> state);

    /**
     * For states held by neurons.
     */
    public abstract static class Computation extends StateVisiting<Value> {

        /**
         * If multiple stateVisitors are visiting the same neuron's state at the same time, they need to have their own view
         * of the state. This is index of such a view, as given by index of the Thread encompassing this StateVisitor.
         */
        public int stateIndex;

        private Computation() {
            stateIndex = -1;
        }

        public Computation(int stateIndex) {
            this.stateIndex = stateIndex;
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

    /**
     * For states held within neural networks' caches.
     */
    public abstract static class Structure<V> extends StateVisiting<V> {
        @Override
        public V visit(State<V> state) {
            LOG.severe("StructureVisitor called for generic State<Structure>.");
            return state.accept(this);
        }
    }
}