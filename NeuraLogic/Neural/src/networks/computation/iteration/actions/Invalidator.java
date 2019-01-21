package networks.computation.iteration.actions;

import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public class Invalidator extends StateVisiting.ComputationVisitor {
    private static final Logger LOG = Logger.getLogger(Invalidator.class.getName());

    public Invalidator(int stateIndex) {
        super(stateIndex);
    }

    @Override
    public boolean ready4visit(State.Neural.Computation state) {
        return true;
    }

    @Override
    public Value visit(State.Neural.Computation state) {
        state.invalidate();
        return null;
    }
}
