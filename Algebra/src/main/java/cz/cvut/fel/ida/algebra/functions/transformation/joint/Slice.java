package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;

import java.util.Arrays;
import java.util.logging.Logger;

public class Slice implements Transformation {
    private static final Logger LOG = Logger.getLogger(Slice.class.getName());

    private final int[] cols;
    private final int[] rows;

    public Slice() {
        cols = null;
        rows = null;
    }

    public Slice(int[] rows, int[] cols) {
        if (cols != null && cols.length != 2) {
            String err = "Unsupported col slice: " + Arrays.toString(cols) + ". Expected exactly two elements.";
            LOG.severe(err);
            throw new ArithmeticException(err);
        }

        if (rows != null && rows.length != 2) {
            String err = "Unsupported row slice: " + Arrays.toString(rows) + ". Expected exactly two elements.";
            LOG.severe(err);
            throw new ArithmeticException(err);
        }

        this.cols = cols;
        this.rows = rows;
    }

    @Override
    public ActivationFcn replaceWithSingleton() {
        return null;
    }

    public Value evaluate(Value combinedInputs) {
        return combinedInputs.slice(this.rows, this.cols);
    }

    public Value differentiate(Value combinedInputs) {
        return null; // Shouldn't be called
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(this);
    }

    @Override
    public boolean changesShape() {
        return true;
    }

    public static class State extends Transformation.State {
        private final Slice slice;

        public State(Slice transformation) {
            super(transformation);

            slice = transformation;
        }

        @Override
        public void invalidate() {
            super.invalidate();
        }

        @Override
        public Value evaluate() {
            return slice.evaluate(input);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            final int[] size = topGradient.size();

            if (size.length == 0) {
                processedGradient = topGradient;

                return;
            }

            final int rowsTo = slice.rows == null ? size[0] : slice.rows[1];
            final int rowsFrom = slice.rows == null ? 0 : slice.rows[0];

            final int colsTo = slice.cols == null ? size[1] : slice.cols[1];
            final int colsFrom = slice.cols == null ? 0 : slice.cols[0];

            final double[] values = topGradient.getAsArray();
            final double[] inputValues = input.getAsArray();
            final double[] outputValues = new double[inputValues.length];

            if (topGradient instanceof VectorValue) {
                boolean orientation = ((VectorValue) topGradient).rowOrientation;

                if (orientation) {
                    System.arraycopy(values, 0, outputValues, colsFrom, values.length);
                } else {
                    System.arraycopy(values, 0, outputValues, rowsFrom, values.length);
                }

                processedGradient = new VectorValue(outputValues, orientation);
                return;
            }

            final int rows = ((MatrixValue) input).rows;
            final int cols = ((MatrixValue) input).cols;
            final int colNum = colsTo - colsFrom;

            for (int i = rowsFrom; i < rowsTo; i++) {
                System.arraycopy(values, (i - rowsFrom) * colNum, outputValues, i * cols + colsFrom, colNum);
            }

            processedGradient = new MatrixValue(outputValues, rows, cols);
        }
    }
}
