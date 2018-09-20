package networks.evaluation.iteration;

import networks.evaluation.values.Value;
import networks.structure.WeightedNeuron;

import java.util.logging.Logger;

public class StatefulNeuron extends WeightedNeuron {
    private static final Logger LOG = Logger.getLogger(StatefulNeuron.class.getName());

    Value accumulatedGradient;
    Value value;

    public StatefulNeuron(String id) {
        super(id);
    }

    public Value getValue() {
        return value;
    }

    public Value getAccumulatedGradient() {
        return accumulatedGradient;
    }
}
