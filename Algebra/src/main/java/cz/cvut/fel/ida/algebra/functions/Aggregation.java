package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.aggregation.*;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class representing general functions that take some (possibly sorted) set of input Values. It is able to evaluate and differentiate.
 * This is a generalization of Activation. If it is sufficient to apply the function to the mere summation of the inputs,
 * i.e. if the inputs are always summed up at first and then some non-linearity applied to the sum, use Activation class instead,
 * which is based on providing existing Function<Double, Double> processing, while here the calculation must be implemented via inheritance.
 */
public interface Aggregation extends Combination, Transformation {
    static final Logger LOG = Logger.getLogger(Aggregation.class.getName());

    /**
     * The mmain characteristic of simple aggregation functions is that they are input permutation invariant (AVG, SUM, etc.)
     *
     * @return
     */
    @Override
    default boolean isInputSymmetric() {
        return true;
    }


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
    }

    public static abstract class State extends Combination.State {

        protected Value combinedInputs;
        protected Value processedGradient;

        public State(Combination combination) {
            super(combination);
        }

        @Override
        public void setupDimensions(Value value) {
            this.combinedInputs = value.getForm();
        }

//        @Override
//        public void ingestTopGradient(Value topGradient) {
//            Value inputFcnDerivative = gradient();
//            processedGradient = topGradient.elementTimes(inputFcnDerivative);       //elementTimes here - since the fcn to be differentiated was applied element-wise on a vector
//        }
    }


//    public static Aggregation.State getAggregationState(Aggregation aggregation) {
//        if (aggregation instanceof CrossSum) {
//            return new CombinationState.CrossSumState((Transformation) aggregation);
//        } else if (aggregation instanceof ElementProduct) {
//            return new CombinationState.ElementProductState((Activation) aggregation);
//        } else if (aggregation instanceof Product) {
//            return new CombinationState.ProductState((Activation) aggregation);
//        } else if (aggregation instanceof Concatenation) {
//            return new CombinationState.ConcatState((Activation) aggregation);
//        } else if (aggregation instanceof Average) {
//            return new Pooling.Avg();
//        } else if (aggregation instanceof Maximum) {
//            return new Pooling.Max();
//        } else if (aggregation instanceof Minimum) {
//            return new Pooling.Min();
//        } else if (aggregation instanceof Sum) {
//            return new Pooling.Sum();
//        } else if (aggregation instanceof Softmax) {
//            return new CombinationState.SoftmaxState((Transformation) aggregation);
//        } else if (aggregation instanceof SharpMax) {
//            return new TransformationState.SharpMaxState();
//        } else if (aggregation instanceof SharpMin) {
//            return new AggregationState.Pooling.AtomMin(((SharpMin) aggregation).activation);
//        } else if (aggregation instanceof Transposition) {
//            return new AggregationState.TranspositionState();
//            return new TransformationState.SharpMinState();
//        } else if (aggregation instanceof Activation) {
//            return new AggregationState.SumState((Activation) aggregation);
//        } else {
//            throw new UnsupportedOperationException("unkown Aggregation function state");
//        }
//    }
}