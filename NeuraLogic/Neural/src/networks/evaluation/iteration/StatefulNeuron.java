package networks.evaluation.iteration;

import networks.evaluation.functions.Activation;
import networks.evaluation.values.Value;
import networks.structure.networks.State;
import networks.structure.neurons.Neuron;

import java.util.logging.Logger;

@Deprecated
public class StatefulNeuron<S> extends Neuron {
    private static final Logger LOG = Logger.getLogger(StatefulNeuron.class.getName());

    Value accumulatedGradient;
    Value value;

    public StatefulNeuron(int index, String id, State state, Activation activation) {
        super(index, id, state, activation);
    }


    public Value getValue() {
        return value;
    }

    public Value getAccumulatedGradient() {
        return accumulatedGradient;
    }
}