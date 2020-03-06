package networks.computation.iteration.visitors.states.neurons;

import evaluation.values.ScalarValue;
import evaluation.values.Value;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.neurons.states.State;
import utils.generic.Pair;

import java.util.logging.Logger;

public class SaturationChecker extends StateVisiting.Computation {
    private static final Logger LOG = Logger.getLogger(SaturationChecker.class.getName());

    @Override
    public Value visit(State.Neural.Computation state) {
        Value value = state.getValue();
        Pair<Double, Double> range = state.getAggregation().getSaturationRange();
        if (range == null) {
            return Value.ZERO;
        }
        ScalarValue lowerBound = new ScalarValue(range.r);
        ScalarValue upperBound = new ScalarValue(range.s);

        if (value.greaterThan(upperBound)) {
            return Value.ONE;
        }
        if (lowerBound.greaterThan(value)) {
            return Value.ONE;
        }
        return Value.ZERO;
    }
}
