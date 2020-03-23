package cz.cvut.fel.ida.old_tests.networks.structure.metadata.states;

import cz.cvut.fel.ida.algebra.functions.specific.Sigmoid;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Evaluator;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;
import org.junit.jupiter.api.Test;

public class StateTest {

    @Test
    public void dynamicDispatch(){
        State.Neural.Computation state = new States.ComputationStateStandard(new Sigmoid());

        Evaluator evaluator = new Evaluator(0);

        Value result = state.getValue();
    }
}