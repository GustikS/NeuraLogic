package networks.structure.metadata.states;

import evaluation.functions.specific.Sigmoid;
import evaluation.values.Value;
import networks.computation.iteration.visitors.states.neurons.Evaluator;
import networks.structure.components.neurons.states.State;
import networks.structure.components.neurons.states.States;
import org.junit.Test;

public class StateTest {

    @Test
    public void dynamicDispatch(){
        State.Neural.Computation state = new States.ComputationStateStandard(new Sigmoid());

        Evaluator evaluator = new Evaluator(0);

        Value result = state.getValue();
    }
}