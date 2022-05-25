package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.aggregation.*;
import cz.cvut.fel.ida.algebra.functions.aggregation.Sum;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class representing general functions that take some (possibly sorted) set of input Values. It is able to evaluate and differentiate.
 * This is a generalization of Activation. If it is sufficient to apply the function to the mere summation of the inputs,
 * i.e. if the inputs are always summed up at first and then some non-linearity applied to the sum, use Activation class instead,
 * which is based on providing existing Function<Double, Double> processing, while here the calculation must be implemented via inheritance.
 */
public abstract class Aggregation implements Exportable {
    private static final Logger LOG = Logger.getLogger(Aggregation.class.getName());

    /**
     * Simply name of the activation function (used for external mapping into DL frameworks)
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * We do not want to create a new object for the same activation function that gets repeated over milions of neurons, even if it's very lightweight
     *
     * @return
     */
    public abstract Aggregation replaceWithSingleton();

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
                return Singletons.average;
            case MAX:
                return Singletons.maximum;
            case MIN:
                return Singletons.minimum;
            case SUM:
                return Singletons.sum;
            case COUNT:
                return Singletons.count;
            default:
                LOG.severe("Unimplemented aggregation function");
                return null;
        }
    }

    public static Aggregation parseFrom(String agg) {
        switch (agg) {
            case "avg":
                return Singletons.average;
            case "max":
                return Singletons.maximum;
            case "min":
                return Singletons.minimum;
            case "sum":
                return Singletons.sum;
            case "count":
                return Singletons.count;
            default:
                throw new RuntimeException("Unable to parse activation function: " + agg);
        }
    }

    /**
     * Return 2 values, lower bound and upper bound, beyond which the function is almost saturated
     *
     * @return
     */
    public Pair<Double, Double> getSaturationRange() {
        return null;    // by default there is no saturation (for Max, Avg, Identity, etc.)
    }

    /**
     * The inputs can be permuted without affecting the result?
     * This may cause some neurons to be equivalent and thus be effectively pruned as such.
     *
     * @return
     */
    public abstract boolean isInputSymmetric();

    public boolean isComplex() {
        return false;
    }

    public static class Singletons {
        public static Average average = new Average();
        public static Maximum maximum = new Maximum();
        public static Minimum minimum = new Minimum();
        public static Sum sum = new Sum();
        public static Count count = new Count();
    }

    /**
     * During neural computation of Aggregation/Activation, a computational State resides in memory for efficient reuse.
     * E.g. we are not given all inputs/outputs at once, but the Values come sequentially as we iterate the neurons.
     * This State is then to accumulate the intermediate Values first, before finally calling the respective functions.
     * <p>
     * This interface applies equally to the Activation subclass, so it is just kept here once.
     * See AggregationState for concrete implementations.
     */
    public interface State extends Exportable {
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
         * Calculate the Value of the current State.
         *
         * @return
         */
        Value evaluate();

        /**
         * Calculate gradient of the current State.
         *
         * @return
         */
        Value gradient();

        /**
         * Calculate gradient w.r.t. the next input
         * @return
         */
        Value nextInputDerivative();
    }
}