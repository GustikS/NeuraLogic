package networks.computation.functions;

import networks.computation.values.Value;
import networks.structure.weights.Weight;

import java.util.ArrayList;

/**
 * Created by gusta on 8.3.17.
 */
public interface Activation {
    Value evaluate(ArrayList<Value> inputs);
    Value evaluate(ArrayList<Value> inputs, ArrayList<Weight> weights);
    Value evaluate(Value summedInputs);

    Value differentiateAt(Value x);
    Activation differentiateGlobally();
}