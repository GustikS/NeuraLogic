package networks.computation.iteration.visitors.states.networks;

import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public class ParentsGetter extends StateVisiting.Structure {
    private static final Logger LOG = Logger.getLogger(ParentsGetter.class.getName());

    public int visit(State.Structure.Parents state) {
        return state.getParentCount();
    }
}
