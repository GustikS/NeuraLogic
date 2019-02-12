package networks.computation.iteration.visitors.states.networks;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * Transfering stored parentsCount in Network to computation state in neuron
 */
public class ParentsTransfer extends StateVisiting.Computation {
    private static final Logger LOG = Logger.getLogger(ParentsTransfer.class.getName());

    public int parentsCount;

    public ParentsTransfer(int stateIndex) {
        super(stateIndex);
    }

    @Override
    public Value visit(State.Neural.Computation state) {
        LOG.warning("ParentsTransfer Default double dispatch call");
        return null;
    }

    /**
     * Target Computation State - rewrite the parentsCount there
     * @param state
     * @return
     */
    public Value visit(State.Neural.Computation.HasParents state) {
        state.setParents(this, parentsCount);
        return null;
    }
}
