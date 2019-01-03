package networks.computation.iteration.actions;

import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 *
 */
public class Evaluator extends StateVisiting.ComputationVisitor {
    private static final Logger LOG = Logger.getLogger(Evaluator.class.getName());

    public Evaluator(int stateIndex) {
        super(stateIndex);
    }

    @Override
    public boolean ready4visit(State.Neural.Computation state) {
        //todo
        return true;
    }

    @Override
    public Value visit(State.Neural.Computation state) {
        Value evaluation = state.getAggregationState().evaluate();
        //with evaluation of this Neuron's Computation State, we also STORE the resulting value for immediate reuse (instead of calling the ActivationState.evaluation (again))
        state.setResult(this, evaluation);
        return evaluation;
    }

}