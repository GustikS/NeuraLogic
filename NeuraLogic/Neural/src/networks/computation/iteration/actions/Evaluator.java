package networks.computation.iteration.actions;

import networks.computation.evaluation.functions.Activation;
import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;

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
    public boolean ready4activation(State.Computation state) {
        return true;    //todo check
    }

    @Override
    public Value activateOutput(State.Computation computation, Activation activation) {
        List<Value> cumulation = computation.getMessages();
        Value evaluate = activation.evaluate(cumulation);
        computation.setResult(this, evaluate);
        return evaluate;
    }

}