package networks.computation.iteration;

import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.Neuron;

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