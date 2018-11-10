package networks.computation.iteration.actions;

import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;
import networks.structure.metadata.states.States;

/**
 * Created by gusta on 8.3.17.
 */
public class Backproper extends StateVisiting.ComputationVisitor {
    Evaluator evaluator;

    public Backproper(int stateIndex, Evaluator evaluator) {
        super(stateIndex);
        this.evaluator = evaluator;
    }

    @Override
    public boolean ready4activation(State.Computation state) {
        return true; //todo check
    }

    public boolean ready4activation(States.ParentCounter state) {
        return state.checked == state.count;
    }

    //todo next - should return a mask over inputs if something like MAX is used, or -1 otherwise
    @Override
    public Value activateOutput(State.Computation state, Activation activation) {
        Value acumGradient = state.getState(this);
        Value summedInputs = state.getState(evaluator);
        Value inputDerivative = activation.differentiate(summedInputs);
        Value currentLevelDerivative = acumGradient.times(inputDerivative);
        //there is no setting (remembering) of the calculated gradient (as opposed to output, which is reused), it is just returned
        return currentLevelDerivative;
    }
}