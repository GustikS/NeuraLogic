package evaluation.functions;

import evaluation.values.Value;

/**
 * Simpler set of requirements than for a proper activation function.
 */
public interface ErrorFcn {
    Value evaluate(Value output, Value target);

    Value differentiate(Value output, Value target);

    ErrorFcn getSingleton();
}
