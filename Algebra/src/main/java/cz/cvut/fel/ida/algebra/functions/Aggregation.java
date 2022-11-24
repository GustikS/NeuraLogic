package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.aggregation.*;
import cz.cvut.fel.ida.algebra.functions.combination.Concatenation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class representing general {@link Combination} functions that take some (unsorted) set of input Values and produce a single Value.
 * These are commonly input permutation invariant and can operate on variable-sized input lists (as opposed to pure Combination fcns).
 * Hence, they can also produce a single gradient Value (to be sent to all the inputs the same).
 *
 * Additionally, these functions can also be used as {@link Transformation}.
 * The interpretation is that the same logic is performed on the individual numbers of the input Value (vector) instead of the list.
 *
 */
public interface Aggregation extends Combination, Transformation {
    static final Logger LOG = Logger.getLogger(Aggregation.class.getName());

    /**
     * The mmain characteristic of simple aggregation functions is that they are input permutation invariant (AVG, SUM, etc.)
     *
     * @return
     */
    @Override
    default boolean isPermutationInvariant() {
        return true;
    }

    default boolean isSplittable() { return false; }

    default int[] aggregableTerms() { return null; }

    /**
     * Given the symmetry, the gradient is a single Value here
     * @param inputs
     * @return
     */
    public abstract Value differentiate(List<Value> inputs);

    public static Aggregation getFunction(Settings.CombinationFcn aggregationFcn) {
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
            case CONCAT:
                return Singletons.concat;
            default:
//                LOG.severe("Unimplemented aggregation function");
                return null;
        }
    }

    public static class Singletons {
        public static Average average = new Average();
        public static Maximum maximum = new Maximum();
        public static Minimum minimum = new Minimum();
        public static Sum sum = new Sum();
        public static Count count = new Count();
        public static Concatenation concat = new Concatenation();
    }

    public static abstract class State extends Combination.State {

        protected Value combinedInputs;
        protected Value processedGradient;

        public State(Combination combination) {
            super(combination);
        }

        @Override
        public Value initEval(List<Value> values) {
            combinedInputs = combination.evaluate(values);
            return combinedInputs;
        }
    }

}