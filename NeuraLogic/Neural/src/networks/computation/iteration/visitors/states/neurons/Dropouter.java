package networks.computation.iteration.visitors.states.neurons;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.logging.Logger;

public class Dropouter extends StateVisiting.Computation {
    private static final Logger LOG = Logger.getLogger(Dropouter.class.getName());
    Settings settings;


    public Dropouter(Settings settings, int stateIndex) {
        super(stateIndex);
        this.settings = settings;
    }

    /**
     * Default double dispatch call.
     * @param state
     * @return
     */
    @Override
    public Value visit(State.Neural.Computation state) {
        LOG.warning("Default double dispatch call");
        return null;
    }

    public Value visit(State.Neural.Computation.HasDropout state) {
        state.setDropout(this);
        return null;
    }
}
