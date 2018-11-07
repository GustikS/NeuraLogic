package networks.computation.iteration.actions;

import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 *
 */
public class Evaluator extends StateVisitor<Value> {
    private static final Logger LOG = Logger.getLogger(Evaluator.class.getName());

    public Evaluator(int stateIndex) {
        super(stateIndex);
    }

    @Override
    public boolean ready4activation(State.Computation state) {
        return true;
    }

    @Override
    public Value activateOutput(State.Computation state, Activation activation) {
        Value cumulation = state.getCumulation(this);
        Value output = activation.evaluate(cumulation);
        state.setOutput(this, output);
        return output;
    }

    @Override
    public Value getOutput(State.Computation state) {
        return state.getOutput(this);
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

    @Override
    public void setOutput(State.Computation state, Value evaluate) {

    }
}