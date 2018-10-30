package networks.computation.iteration.actions;

import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * Serves 3 purposes: (split into separate classes?)
 * 1) a high level invocation of evaluation of a neural network with query neuron.
 * 2) a neuron (state) visitor - propagating output value into parents - is inherited
 * 3) a bottom-up iterator of neural networks - is outsourced via composition
 */
public class Evaluator extends StateVisitor<Value> {
    private static final Logger LOG = Logger.getLogger(Evaluator.class.getName());

    public Evaluator(int stateIndex) {
        super(stateIndex);
    }

    @Override
    public boolean ready4activation(State.Computation state) {
        return false;
    }

    @Override
    public Value activateOutput(State.Computation activation, Activation state) {
        return null;
    }

    @Override
    public Value getOutput(State.Computation state) {
        return null;
    }

    @Override
    public Value getCumulation(State.Computation state) {
        return null;
    }

    @Override
    public void cumulate(Value from, State.Computation to) {

    }

    @Override
    public void cumulate(Value from, State.Computation to, Weight weight) {

    }
}