package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.setup.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 */
public class Evaluator extends StateVisiting.Computation {
    private static final Logger LOG = Logger.getLogger(Evaluator.class.getName());

    public Evaluator(int stateIndex) {
        super(stateIndex);
    }

    @Override
    public Value visit(State.Neural.Computation state) {
        Value evaluation = state.getAggregationState().evaluate();
        //with evaluation of this Neuron's Computation State, we also STORE the resulting value for immediate reuse (instead of calling the ActivationState.evaluation (again))
        state.setValue(evaluation);
        return evaluation;
    }

    /**
     * Get possibly different StateVisitors of Evaluator's type to manipulate Neurons' States
     *
     * @param settings
     * @return
     */
    public static Evaluator getFrom(Settings settings, int index) {
        return new Evaluator(index);   //todo base on settings
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

    public static class one {
        //
    }

}