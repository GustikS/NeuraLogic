package networks.structure.metadata.states;

import networks.computation.evaluation.functions.specific.Sigmoid;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.states.neurons.Evaluator;
import org.junit.Test;

public class StateTest {

    @Test
    public void dynamicDispatch(){
        State.Neural.Computation state = new States.ComputationStateStandard(new Sigmoid());

        Evaluator evaluator = new Evaluator(0);

        Value result = state.getValue();
    }
}