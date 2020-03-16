package cz.cvut.fel.ida.neural.networks.computation.training.strategies.Hyperparameters;

import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

/**
 * Basic dropout rate setup - decreasing dropout rate with increasing depth
 */
public class DropoutRateStrategy {
    private static final Logger LOG = Logger.getLogger(DropoutRateStrategy.class.getName());
    private final Settings settings;

    public DropoutRateStrategy(Settings settings) {
        this.settings = settings;
    }

    public void setDropout(BaseNeuron neuron) {
        State.Neural.Computation computationView = neuron.getComputationView(0);

        if (computationView instanceof State.Neural.Computation.HasDropout) {
            State.Neural.Computation.HasDropout hasDropout = (State.Neural.Computation.HasDropout) computationView;
            if (hasDropout.getDropoutRate(null) == 0) { //dropout not yet set
                if (neuron.getRawState() instanceof States.ComputationStateComposite) {
                    LOG.severe("Dropout not yet set for a parallel state! You should setup the state first and THEN make it parallel.");
                }
                hasDropout.setDropoutRate(getDropoutRate(neuron.layer));
            } else {
                //if dropout already set we do nothing todo this is not really correct as the same neuron will have different depths at different networks - move dropout to network state then...
            }
        }
    }

    private double getDropoutRate(int layer) {
        return settings.dropoutRate / layer;
    }
}
