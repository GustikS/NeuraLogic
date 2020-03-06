package networks.computation.iteration.visitors.states.neurons;

import evaluation.values.Value;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.neurons.states.State;
import settings.Settings;

import java.util.logging.Logger;

public class Dropouter extends StateVisiting.Computation.Detailed {
    private static final Logger LOG = Logger.getLogger(Dropouter.class.getName());
    Settings settings;


    public Dropouter(Settings settings, int stateIndex) {
        super(stateIndex);
        this.settings = settings;
    }

    @Override
    public Value visit(State.Neural.Computation state) {
        LOG.severe("Default double dispatch call");
        return null;
    }

    @Override
    public Value visit(State.Neural.Computation.HasParents state) {
        LOG.severe("incompetent dispatch call");
        return null;
    }

    public Value visit(State.Neural.Computation.HasDropout state) {
        state.setDropout(this);
        return null;
    }

    @Override
    public Value visit(State.Neural.Computation.Detailed state) {
        LOG.severe("incompetent dispatch call");
        return null;
    }
}
