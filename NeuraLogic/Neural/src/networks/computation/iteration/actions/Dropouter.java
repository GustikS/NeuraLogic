package networks.computation.iteration.actions;

import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.logging.Logger;

public class Dropouter extends StateVisiting.ComputationVisitor {
    private static final Logger LOG = Logger.getLogger(Dropouter.class.getName());
    Settings settings;


    public Dropouter(Settings settings, int stateIndex) {
        super(stateIndex);
        this.settings = settings;
    }

    @Override
    public boolean ready4visit(State.Neural.Computation state) {
        return true;
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
        if (settings.random.nextDouble() < settings.dropoutRate)
            state.setDropout(this, true);
        return null;
    }
}
