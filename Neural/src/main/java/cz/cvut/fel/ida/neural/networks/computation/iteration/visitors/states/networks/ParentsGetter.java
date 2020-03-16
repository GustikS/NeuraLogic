package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.networks;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

public class ParentsGetter extends StateVisiting.Structure {
    private static final Logger LOG = Logger.getLogger(ParentsGetter.class.getName());

    public int visit(State.Structure.Parents state) {
        return state.getParentCount();
    }
}
