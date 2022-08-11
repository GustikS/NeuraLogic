package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.algebra.functions.aggregation.Sum;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.Sigmoid;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Evaluator;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StateTest {
    private static final Logger LOG = Logger.getLogger(StateTest.class.getName());

    @TestAnnotations.Fast
    public void dynamicDispatch() {
        State.Neural.Computation state = new States.ComputationStateStandard(new Sum(), new Sigmoid());
        List<Value> inputValues = new ArrayList<>();
        inputValues.add(new ScalarValue(0));
        state.initEval(inputValues);
//        state.invalidate();
        Evaluator evaluator = new Evaluator(-1);
        Value value = state.getValue();
        Value output = evaluator.visit(state);
        assertEquals(value, new ScalarValue(0.5));
        assertEquals(output, new ScalarValue(0.5));
    }
}