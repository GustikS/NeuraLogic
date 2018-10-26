package networks.computation.iteration.actions;

import networks.computation.functions.Activation;
import networks.computation.training.evaluation.values.Value;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class Backproper extends StateVisitor<Value> {

    List<Weight> allWeights;

    public Backproper(int stateIndex) {
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