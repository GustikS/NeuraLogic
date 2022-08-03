package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;

/**
 * Simpler set of requirements than for a proper activation function.
 */
public interface ErrorFcn {

    Value evaluate(Value output, Value target);

    Value differentiate(Value output, Value target);

    ErrorFcn getSingleton();
}
