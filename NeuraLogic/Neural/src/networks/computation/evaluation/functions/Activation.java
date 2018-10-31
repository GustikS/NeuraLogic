package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;

import java.util.ArrayList;

/**
 * Class representing various activation and also aggregation functions, since there are the same requirements for both.
 * That is evaluation and differentiation.
 *
 * Created by gusta on 8.3.17.
 */
public interface Activation {
    Value evaluate(ArrayList<Value> inputs);

    Value evaluate(ArrayList<Value> inputs, ArrayList<Weight> weights);

    Value evaluate(Value summedInputs);

    Value differentiateAt(Value x);

    Activation differentiateGlobally();
}
