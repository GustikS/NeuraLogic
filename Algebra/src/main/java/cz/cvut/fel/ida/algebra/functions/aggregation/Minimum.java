package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;

public class Minimum implements Aggregation {
    private static final Logger LOG = Logger.getLogger(Minimum.class.getName());

    @Override
    public Minimum replaceWithSingleton() {
        return Singletons.minimum;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value min = inputs.get(0);
        for (int i = 1; i < inputs.size(); i++) {
            Value value;
            if (min.greaterThan(value = inputs.get(i))) {
                min = value;
            }
        }
        return min;
    }

    /**
     * Should not be used as is
     * - needs to be taken care of by the iterator!
     *
     * @param inputs
     * @return
     */
    @Override
    public Value differentiate(List<Value> inputs) {
        return Value.ONE;
    }

    /**
     * Returns a one-hot vector denoting the input minimum value (not just index)
     *
     * @param combinedInputs
     * @return
     */
    @Override
    public Value evaluate(Value combinedInputs) {
        if (combinedInputs instanceof VectorValue) {
            double[] minValue = getMinValue(((VectorValue) combinedInputs).values).s;
            return new VectorValue(minValue);
        } else {
            LOG.severe("Cannot calculate Min from other than VectorValue");
            return null;
        }
    }

    /**
     * Inefficient, but should not be used - see the TransformationState
     *
     * @param combinedInputs
     * @return
     */
    @Override
    public Value differentiate(Value combinedInputs) {
        Value oneHot = evaluate(combinedInputs).apply(x -> x > 0 ? 1.0 : 0.0);
        return oneHot;
    }

    /**
     * returns a one-hot vector denoting the input minimum value (not just index)
     *
     * @param input
     * @return
     */
    public Pair<Integer, double[]> getMinValue(double[] input) {
        double min = Double.MAX_VALUE;
        int min_index = -1;
        for (int i = 0; i < input.length; i++) {
            if (input[i] < min) {
                min = input[i];
                min_index = i;
            }
        }
        double[] result = new double[input.length];
        result[min_index] = min;
        return new Pair<>(min_index, result);
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        if (singleInput)
            return new Minimum.TransformationState(Singletons.minimum);
        else
            return new Minimum.AggregationState(Singletons.minimum);
    }

    public static class AggregationState extends Aggregation.State {

        int minIndex = -1;
        int currentIndex = 0;
        //        Value minValue;   == combinedInputs

        public AggregationState(Combination combination) {
            super(combination);
        }

        @Override
        public void cumulate(Value value) {
            if (combinedInputs == null || combinedInputs.greaterThan(value)) {
                combinedInputs = value;
                minIndex = currentIndex;
            }
            currentIndex++;
        }

        @Override
        public Value evaluate() {
            return combinedInputs;
        }

        @Override
        public void invalidate() {
            minIndex = -1;
            currentIndex = 0;
            combinedInputs = null;
        }

        @Override
        public int[] getInputMask() {
            int[] inputs = new int[1];
            inputs[0] = minIndex;
            return inputs;
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            currentIndex = 0;   //setup for the upcoming iteration
            processedGradient = topGradient;
        }

        /**
         * A version of Min for Topologic backprop where all the inputs are to be iterated
         *
         * @return
         */
        @Override
        public Value nextInputDerivative() {
//            LOG.warning("Calling nextInputDerivative in Min pooling");
//            return null;
            if (minIndex == currentIndex++) {
                return processedGradient;
            } else {
                return Value.ZERO;
            }
        }
    }

    public static class TransformationState extends Transformation.State {

        int minIndex;

        public TransformationState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            minIndex = -1;
        }

        @Override
        public Value evaluate() {
            VectorValue inputVector = (VectorValue) input;
            Pair<Integer, double[]> mminValue = Singletons.minimum.getMinValue(inputVector.values);
            minIndex = mminValue.r;
            return new VectorValue(mminValue.s, inputVector.rowOrientation);
        }

        /**
         * Only propagate the value at the minIndex
         *
         * @param topGradient
         */
        @Override
        public void ingestTopGradient(Value topGradient) {
            VectorValue gradient = ((VectorValue) input).getForm();
            gradient.set(minIndex, topGradient.get(minIndex));
            processedGradient = gradient;
        }
    }
}
