package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Class representing general functions that take some input Values. It is able to evaluate and differentiate.
 */
public interface Aggregation {

    Value evaluate(List<Value> inputs);

    Value differentiate(List<Value> inputs);

    /**
     * This should probably be optional, if not needed anywhere.
     *
     * @return
     */
    @Nullable
    @Deprecated
    Activation differentiateGlobally();

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