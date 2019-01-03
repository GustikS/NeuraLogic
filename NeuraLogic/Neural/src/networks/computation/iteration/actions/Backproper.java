package networks.computation.iteration.actions;

import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Backproper extends StateVisiting.ComputationVisitor {
    private static final Logger LOG = Logger.getLogger(Backproper.class.getName());

    Evaluator evaluator;

    public Backproper(int stateIndex, Evaluator evaluator) {
        super(stateIndex);
        this.evaluator = evaluator;
    }

    @Override
    public boolean ready4visit(State.Neural.Computation state) {
        return true; //todo check
    }

    public boolean ready4visit(State.Neural.Computation.HasParents state) {
        return state.getChecked(this) == state.getParents(this);
    }

    //todo next - should return a mask over inputs if something like MAX is used, or -1 otherwise
    @Override
    public Value visit(State.Neural.Computation state) {
        Value acumGradient = state.getResult(this); //top-down accumulation
        Value inputDerivative = state.getAggregationState().gradient(); //bottom-up accumulation

        Value currentLevelDerivative = acumGradient.times(inputDerivative);
        //there is no setting (remembering) of the calculated gradient (as opposed to output, which is reused), it is just returned
        return currentLevelDerivative;//todo next - where should we invalidate?
    }

}