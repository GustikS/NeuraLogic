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

public class Maximum implements Aggregation {
    private static final Logger LOG = Logger.getLogger(Maximum.class.getName());

    @Override
    public Maximum replaceWithSingleton() {
        return Aggregation.Singletons.maximum;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        Value max = inputs.get(0);
        for (int i = 1; i < inputs.size(); i++) {
            Value value;
            if ((value = inputs.get(i)).greaterThan(max)) {
                max = value;
            }
        }
        return max;
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
     * Returns a one-hot vector denoting the input maximum value (not just index)
     *
     * @param combinedInputs
     * @return
     */
    @Override
    public Value evaluate(Value combinedInputs) {
        if (combinedInputs instanceof VectorValue) {
            double[] maxValue = getMaxValue(((VectorValue) combinedInputs).values).s;
            return new VectorValue(maxValue);
        } else {
            LOG.severe("Cannot calculate Max from other than VectorValue");
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
     * returns a one-hot vector denoting the input maximum value (not just index)
     *
     * @param input
     * @return
     */
    public Pair<Integer, double[]> getMaxValue(double[] input) {
        double max = Double.MIN_VALUE;
        int max_index = -1;
        for (int i = 0; i < input.length; i++) {
            if (input[i] > max) {
                max = input[i];
                max_index = i;
            }
        }
        double[] result = new double[input.length];
        result[max_index] = max;
        return new Pair<>(max_index, result);
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }


    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        if (singleInput)
            return new TransformationState(Singletons.maximum);
        else
            return new AggregationState(Singletons.maximum);
    }

    public static class AggregationState extends Aggregation.State {
        int maxIndex = -1;
        int currentIndex = 0;
//        Value maxValue;   == combinedInputs

        public AggregationState(Combination combination) {
            super(combination);
        }

        @Override
        public void cumulate(Value value) {
            if (combinedInputs == null || value.greaterThan(combinedInputs)) {
                combinedInputs = value;
                maxIndex = currentIndex;
            }
            currentIndex++;
        }

        @Override
        public Value evaluate() {
            return combinedInputs;
        }


        @Override
        public void invalidate() {
            maxIndex = -1;
            currentIndex = 0;
            combinedInputs = null;
        }

        @Override
        public int[] getInputMask() {
            int[] inputs = new int[1];
            inputs[0] = maxIndex;
            return inputs;
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            currentIndex = 0;   //setup for the upcoming iteration
            processedGradient = topGradient;
        }

        /**
         * A version of Max for Topologic backprop where all the inputs are to be iterated
         *
         * @return
         */
        @Override
        public Value nextInputDerivative() {
//            LOG.warning("Calling nextInputDerivative in Max pooling");
//            return null;
            if (maxIndex == currentIndex++) {
                return processedGradient;
            } else {
                return Value.ZERO;
            }
        }
    }

    public static class TransformationState extends Transformation.State {

        int maxIndex;

        public TransformationState(Transformation transformation) {
            super(transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            maxIndex = -1;
        }

        @Override
        public Value evaluate() {
            VectorValue inputVector = (VectorValue) input;
            Pair<Integer, double[]> maxValue = Singletons.maximum.getMaxValue(inputVector.values);
            maxIndex = maxValue.r;
            return new VectorValue(maxValue.s, inputVector.rowOrientation);
        }

        /**
         * Only propagate the value at the maxIndex
         *
         * @param topGradient
         */
        @Override
        public void ingestTopGradient(Value topGradient) {
            VectorValue gradient = ((VectorValue) input).getForm();
            gradient.set(maxIndex, topGradient.get(maxIndex));
            processedGradient = gradient;
        }
    }
}
