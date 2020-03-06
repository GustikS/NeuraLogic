package networks.computation.iteration.visitors.states.neurons;

import evaluation.values.Value;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.neurons.states.State;

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
