package networks.evaluation.iteration;

import networks.evaluation.values.Value;
import networks.structure.Neuron;

import java.util.logging.Logger;

public class StatefulNeuron extends Neuron {
    private static final Logger LOG = Logger.getLogger(StatefulNeuron.class.getName());

    double accumulatedGradient;
    double value;

    public StatefulNeuron(String id) {
        super(id);
    }

    @Override
    public Value evaluate() {
        return null;
    }

    @Override
    public Value gradient() {
        return null;
    }
}
