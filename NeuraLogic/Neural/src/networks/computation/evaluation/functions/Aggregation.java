package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;

import java.util.List;

/**
 * Class representing general functions that take some (possibly sorted) set of input Values. It is able to evaluate and differentiate.
 * This is a generalization of ActivationFunction.
 */
public abstract class Aggregation {

    public abstract Value evaluate(List<Value> inputs);

    public abstract Value differentiate(List<Value> inputs);

    /**
     * During neural computation of Aggregation/Activation, a computational State resides in memory for efficient reuse.
     * E.g. we are not given all inputs/outputs at once, but the Values come sequentially as we iterate the neurons.
     * The State is then to accumulate the intermediate Values first, before finally calling the respective functions.
     */
    interface State {
        /**
         * Store a value - add it to the current state
         *
         * @param value
         */
        void cumulate(Value value);

        void invalidate();

        int[] getInputMask();

        Value gradient();

        Value evaluate();
    }
}