package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;

public class SaturationChecker extends StateVisiting.Computation {
    private static final Logger LOG = Logger.getLogger(SaturationChecker.class.getName());

    @Override
    public Value visit(State.Neural.Computation state) {
        Value value = state.getValue();

        if (value.isNaN()) {
            throw new RuntimeException("NaN value " + value.toDetailedString() + "  encountered during saturation check at neuron state " + state.toString());
        }

        if (state.getTransformation() == null) {
            return Value.ZERO;
        }

        Pair<Double, Double> range = state.getTransformation().getSaturationRange();
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
