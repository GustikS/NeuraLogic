package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

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

        protected Computation() {
            stateIndex = -1;
        }

        public Computation(int stateIndex) {
            this.stateIndex = stateIndex;
        }

        @Override
        public Value visit(State<Value> state) {
            LOG.severe("Default double dispatch - computationVisitor called for generic State<Value>.");
            return visit((State.Neural.Computation) state);
        }

        /**
         * Transformation of the State of this neuron through its activation function into a result Value
         *
         * @param state
         * @return
         */
        public abstract Value visit(State.Neural.Computation state);

        /**
         * To be used when individual detailed interfaces of the state need to be recognized by the visitor.
         */
        public abstract static class Detailed extends StateVisiting.Computation {

            public Detailed(int stateIndex) {
                super(stateIndex);
            }

            public abstract Value visit(State.Neural.Computation.HasParents state);

            public abstract Value visit(State.Neural.Computation.HasDropout state);

            public abstract Value visit(State.Neural.Computation.Detailed state);
        }
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