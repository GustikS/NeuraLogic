package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

public class Invalidator extends StateVisiting.Computation {
    private static final Logger LOG = Logger.getLogger(Invalidator.class.getName());

    public Invalidator(int stateIndex) {
        super(stateIndex);
    }

    @Override
    public Value visit(State.Neural.Computation state) {
        state.invalidate();
        return null;
    }
}
