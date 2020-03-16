package networks.structure.metadata.states;

import cz.cvut.fel.ida.algebra.evaluation.functions.specific.Sigmoid;
import cz.cvut.fel.ida.algebra.evaluation.values.Value;
import cz.cvut.fel.ida.pipelines.networks.computation.iteration.visitors.states.neurons.Evaluator;
import cz.cvut.fel.ida.pipelines.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.pipelines.networks.structure.components.neurons.states.States;
import org.junit.Test;

public class StateTest {

    @Test
    public void dynamicDispatch(){
        State.Neural.Computation state = new States.ComputationStateStandard(new Sigmoid());

        Evaluator evaluator = new Evaluator(0);

        Value result = state.getValue();
    }
}