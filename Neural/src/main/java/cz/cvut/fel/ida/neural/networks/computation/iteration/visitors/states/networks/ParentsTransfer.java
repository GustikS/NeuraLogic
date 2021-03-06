package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.networks;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

/**
 * Transfering stored parentsCount in Network to computation state in neuron
 */
public class ParentsTransfer extends StateVisiting.Computation.Detailed {
    private static final Logger LOG = Logger.getLogger(ParentsTransfer.class.getName());

    public int parentsCount;

    public ParentsTransfer(int stateIndex) {
        super(stateIndex);
    }

    @Override
    public Value visit(State.Neural.Computation state) {
        LOG.severe("ParentsTransfer Default double dispatch call");
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

    @Override
    public Value visit(State.Neural.Computation.HasDropout state) {
        LOG.severe("incompetent dispatch call");
        return null;
    }

    @Override
    public Value visit(State.Neural.Computation.Detailed state) {
        LOG.severe("incompetent dispatch call");
        return null;
    }
}
