package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;

import java.util.ArrayList;

/**
 * Created by gusta on 8.3.17.
 */
public class Sigmoid implements Activation {

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