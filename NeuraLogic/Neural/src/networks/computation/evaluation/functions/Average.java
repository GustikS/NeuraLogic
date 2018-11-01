package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Average implements Activation {
    private static final Logger LOG = Logger.getLogger(Average.class.getName());

    @Override
    public Value evaluate(ArrayList<Value> inputs) {
        return null;
    }

    @Override
    public Value evaluate(ArrayList<Value> inputs, ArrayList<Weight> weights) {
        return null;
    }

    @Override
    public Value evaluate(Value summedInputs) {
        return null;
    }

    @Override
    public Value differentiateAt(Value x) {
        return null;
    }

    @Override
    public Activation differentiateGlobally() {
        return null;
    }
}
