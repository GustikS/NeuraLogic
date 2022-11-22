package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * It flattens/linearizes the input Values, hence the result is always a VectorValue!
 * - to avoid the necessity to specify along which dimension to do the concat...       //todo make a parameterized version
 * <p>
 * Can be used also as an Aggregation now - i.e. combining variable-sized input sets - use with care,
 * since this means generally not knowing the output dimensionality of the corresponding neuron (+random, but constant, order of elements)
 */
public class Concatenation implements Combination, Aggregation {
    private static final Logger LOG = Logger.getLogger(Concatenation.class.getName());

    private final int axis;

    public Concatenation() {
        this.axis = -1; // axis -1 = flatten
    }

    public Concatenation(int axis) {
        this.axis = axis;

        if (axis != -1 && axis != 0) {
            String err = "Unsupported concatenation axis: " + axis + ". Expected either -1 or 0.";
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
    }

    @Override
    public Combination replaceWithSingleton() {
        return Combination.Singletons.concatenation;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        List<Double> concat = new ArrayList<>();

        if (axis == -1) {
            for (Value input : inputs) {
                for (Double val : input) {
                    concat.add(val);
                }
            }

            return new VectorValue(concat);
        }

        final int[] dimensions = inputs.get(0).size();
        final int colSize = dimensions.length == 2 ? dimensions[1] : 1;

        for (Value input : inputs) {
            final int[] valSize = input.size();

            if ((valSize.length == 0 && colSize != 1) || (valSize.length == 2 && valSize[1] != colSize)) {
                String err = "Cannot concatenate value with size: " + Arrays.toString(valSize) + ". Expected " + colSize + " columns.";
                LOG.severe(err);
                throw new ArithmeticException(err);
            }

            for (Double val : input) {
                concat.add(val);
            }
        }

        double[] concatValues = concat.stream().mapToDouble(d -> d).toArray();

        if (colSize == 1) {
            return new VectorValue(concatValues);
        }

        return new MatrixValue(concatValues, concatValues.length / colSize, colSize);
    }

    @Override
    public Value differentiate(List<Value> inputs) {
        LOG.warning("Directly calculating derivative of CONCAT fcn");
        return Value.ONE;
    }

    @Override
    public Value evaluate(Value combinedInputs) {
        LOG.warning("Directly evaluating CONCAT fcn on a single input");
        return combinedInputs;  // no effect
    }

    @Override
    public Value differentiate(Value combinedInputs) {
        LOG.warning("Directly calculating derivative of CONCAT on a single input");
        return Value.ONE;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public boolean isPermutationInvariant() {
        return false;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(this);
    }

    public static class State extends Combination.InputArrayState {

        Concatenation concatenation;

        int processedIndex = 0;

        public State(Concatenation combination) {
            super(combination);

            this.concatenation = combination;
        }

        @Override
        public Value evaluate() {
            return concatenation.evaluate(accumulatedInputs);
        }

        @Override
        public void invalidate() {
            super.invalidate();

            processedIndex = 0;
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
        }

        /**
         * Just sending down the next scalar from the gradient vector
         *
         * @return
         */
        @Override
        public Value nextInputGradient() {
            Value value = this.accumulatedInputs.get(i++);

            if (value instanceof ScalarValue) {
                return new ScalarValue(processedGradient.get(processedIndex++));
            }

            final int size = value.getAsArray().length;
            final double[] slicedGradient = new double[size];

            System.arraycopy(processedGradient.getAsArray(), processedIndex, slicedGradient, 0, size);
            processedIndex += size;

            if (value instanceof VectorValue) {
                VectorValue v = (VectorValue) value;

                return new VectorValue(slicedGradient, v.rowOrientation);
            }

            MatrixValue m = (MatrixValue) value;
            return new MatrixValue(slicedGradient, m.rows, m.cols);
        }
    }
}
