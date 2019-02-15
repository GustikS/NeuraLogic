package networks.computation.evaluation.functions;

import networks.computation.evaluation.functions.specific.Average;
import networks.computation.evaluation.functions.specific.Maximum;
import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.AggregationState;
import settings.Settings;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class representing general functions that take some (possibly sorted) set of input Values. It is able to evaluate and differentiate.
 * This is a generalization of Activation. If it is sufficient to apply the function to the mere summation of the inputs,
 * i.e. if the inputs are always summed up at first and then some non-linearity applied to the sum, use Activation class instead,
 * which is based on providing existing Function<Double, Double> processing, while here the calculation must be implemented via inheritance.
 */
public abstract class Aggregation {
    private static final Logger LOG = Logger.getLogger(Aggregation.class.getName());

    /**
     * Return the result of corresponding Aggregation function applied to the list of inputs.
     *
     * @param inputs
     * @return
     */
    public abstract Value evaluate(List<Value> inputs);

    public abstract Value differentiate(List<Value> inputs);

    public static Aggregation getAggregation(Settings.AggregationFcn aggregationFcn) {
        switch (aggregationFcn) {
            case AVG:
                return new Average();
            case MAX:
                return new Maximum();
            //todo rest
            default:
                LOG.severe("Unimplemented aggregation function");
                return null;
        }
    }

    public abstract AggregationState getAggregationState();

    /**
     * During neural computation of Aggregation/Activation, a computational State resides in memory for efficient reuse.
     * E.g. we are not given all inputs/outputs at once, but the Values come sequentially as we iterate the neurons.
     * This State is then to accumulate the intermediate Values first, before finally calling the respective functions.
     * <p>
     * This interface applies equally to the Activation subclass, so it is just kept here once.
     * See AggregationState for concrete implementations.
     */
    public interface State {
        /**
         * Store a value - add it to the current state
         *
         * @param value
         */
        void cumulate(Value value);

        /**
         * Reset all intermediate results of calculation (typically by zeroing them out)
         */
        void invalidate();

        /**
         * If the activation applies to inly a subset of the input values, this return an array of the corresponding indices.
         * Otherwise returns null if the activation is based on all the inputs.
         *
         * @return
         */
        int[] getInputMask();

        /**
         * Calculate gradient of the current State.
         *
         * @return
         */
        Value gradient();

        /**
         * Calculate the Value of the current State.
         *
         * @return
         */
        Value evaluate();
    }
}