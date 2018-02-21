package networks.evaluation.functions;

import networks.evaluation.values.Value;

/**
 * Created by gusta on 8.3.17.
 */
public interface Activation {
    Value evaluate();
    Value differentiateAt(Value x);
    Activation differentiateGlobally();
}