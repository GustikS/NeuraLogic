package networks.computation.iteration.actions;

import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
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


    /**
     * Get possibly different StateVisitors of Evaluator's type to manipulate Neurons' States
     *
     * @param settings
     * @return
     */
    public static Evaluator getFrom(Settings settings, int index) {
        return new Evaluator(index);   //todo
    }

    /**
     * Get multiple evaluators with different state access views/indices
     *
     * @param settings
     * @param count
     * @return
     */
    @Deprecated
    public static List<Evaluator> getParallelEvaluators(Settings settings, int count) {
        List<Evaluator> evaluators = new ArrayList<>(count);
        for (int i = 0; i < evaluators.size(); i++) {
            evaluators.add(i, new Evaluator(i));
        }
        return evaluators;
    }

}