package networks.computation.evaluation.values;

import com.sun.istack.internal.NotNull;
import networks.computation.evaluation.values.distributions.ValueInitializer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * By default we consider matrices stored row-wise, i.e. M[rows][cols].
 *
 * @see VectorValue
 * <p>
 * Created by gusta on 8.3.17.
 */
public class MatrixValue extends Value {
    private static final Logger LOG = Logger.getLogger(MatrixValue.class.getName());

    public int rows;
    public int cols;

    /**
     * The actual values
     */
    public double[][] values;

    public MatrixValue(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        values = new double[rows][cols];
    }


    @NotNull
    @Override
    public Iterator<Double> iterator() {
        return new MatrixValue.ValueIterator();
    }

    /**
     * The default iteration is row-wise, i.e. all the elements from first row go before all the elements from second rows etc., just like the default storage of a matrix.
     */
    protected class ValueIterator implements Iterator<Double> {
        int row;
        int col;

        final int maxCol = cols - 1;
        final int maxRow = rows - 1;

        @Override
        public boolean hasNext() {
            return row < maxRow || col < maxCol;
        }

        @Override
        public Double next() {
            double next = values[row][col];
            if (col < cols - 1)
                col++;
            else {
                row++;
                col = 0;
            }
            return next;
        }
    }

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        valueInitializer.initMatrix(this);
    }

    @Override
    public MatrixValue zero() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i][j] = 0;
            }
        }
        return this;
    }

    @Override
    public MatrixValue clone() {
        MatrixValue clone = new MatrixValue(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                clone.values[i][j] = values[i][j];
            }
        }
        return clone;
    }

    @Override
    public MatrixValue getForm() {
        return new MatrixValue(rows, cols);
    }

    @Override
    public int[] size() {
        return new int[]{rows, cols};
    }

    @Override
    public Value apply(Function<Double, Double> function) {
        MatrixValue result = new MatrixValue(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.values[i][j] = function.apply(values[i][j]);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(values);
    }

    /**
     * DDD
     *
     * @param value
     * @return
     */
    @Override
    public Value times(Value value) {
        return value.times(this);
    }

    @Override
    public MatrixValue times(ScalarValue value) {
        MatrixValue clone = this.clone();
        double value1 = value.value;
        for (int i = 0; i < clone.rows; i++) {
            for (int j = 0; j < clone.cols; j++) {
                clone.values[i][j] *= value1;
            }
        }
        return clone;
    }

    @Override
    public VectorValue times(VectorValue value) {
        if (rows != value.values.length) {
            LOG.severe("Matrix row length mismatch with vector length for multiplication");
        }
        VectorValue result = new VectorValue(cols);
        double[] resultValues = result.values;
        double[] origValues = value.values;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                resultValues[i] += origValues[j] * values[j][i];
            }
        }
        return result;
    }

    /**
     * Remember that the double-dispatch is changing rhs and lhs sides
     * <p>
     * MatrixValue lhs = value;
     * MatrixValue rhs = this;
     * <p>
     * <p>
     * todo use the Strassen algorithm for bigger matrices or just outsource to eigen!
     *
     * @param value
     * @return
     */
    @Override
    public MatrixValue times(MatrixValue value) {
        if (value.cols != rows) {
            LOG.severe("Matrix to matrix dimension mismatch for multiplication");
        }
        MatrixValue result = new MatrixValue(value.rows, this.cols);
        double[][] lhs = value.values;

        double[][] resultValues = result.values;
        for (int i = 0; i < value.rows; i++) {         // rows from lhs
            for (int j = 0; j < cols; j++) {     // columns from rhs
                for (int k = 0; k < value.cols; k++) { // columns from lhs
                    resultValues[i][j] += lhs[i][k] * values[k][j];
                }
            }
        }
        return result;
    }

    /**
     * DDD
     *
     * @param value
     * @return
     */
    @Override
    public Value plus(Value value) {
        return value.plus(this);
    }

    @Override
    public MatrixValue plus(ScalarValue value) {
        MatrixValue clone = clone();
        double value1 = value.value;
        for (int i = 0; i < clone.rows; i++) {
            for (int j = 0; j < clone.cols; j++) {
                clone.values[i][j] += value1;
            }
        }
        return clone;
    }

    @Override
    public Value plus(VectorValue value) {
        LOG.severe("Incompatible summation of matrix plus vector ");
        return null;
    }

    @Override
    public Value plus(MatrixValue value) {
        if (rows != value.rows || cols != value.cols) {
            LOG.severe("Incompatible summation of matrix plus matrix ");
        }
        MatrixValue result = new MatrixValue(rows, cols);
        double[][] resultValues = result.values;
        double[][] otherValues = value.values;
        for (int i = 0; i < result.rows; i++) {
            for (int j = 0; j < result.cols; j++) {
                resultValues[i][j] = this.values[i][j] + otherValues[i][j];
            }
        }
        return result;
    }

    /**
     * DDD
     *
     * @param value
     * @return
     */
    @Override
    public Value minus(Value value) {
        return value.minus(this);
    }

    @Override
    public Value minus(ScalarValue value) {
        MatrixValue result = new MatrixValue(rows, cols);
        double[][] resultValues = result.values;
        double value1 = value.value;
        for (int i = 0; i < result.rows; i++) {
            for (int j = 0; j < result.cols; j++) {
                resultValues[i][j] = value1 - values[i][j];
            }
        }
        return result;
    }

    @Override
    public Value minus(VectorValue value) {
        LOG.severe("Incompatible dimensions of algebraic operation - vector minus matrix");
        return null;
    }

    @Override
    public Value minus(MatrixValue value) {
        if (rows != value.rows || cols != value.cols) {
            LOG.severe("Incompatible subtracting of matrix minus matrix ");
        }
        MatrixValue result = new MatrixValue(rows, cols);
        double[][] resultValues = result.values;
        double[][] otherValues = value.values;
        for (int i = 0; i < result.rows; i++) {
            for (int j = 0; j < result.cols; j++) {
                resultValues[i][j] = otherValues[i][j] - this.values[i][j];
            }
        }
        return result;
    }

    /**
     * DDD
     *
     * @param value
     */
    @Override
    public void increment(Value value) {
        value.increment(this);
    }

    @Override
    public void increment(ScalarValue value) {
        LOG.severe("Incompatible dimensions of algebraic operation - scalar increment by matrix");
    }

    @Override
    public void increment(VectorValue value) {
        LOG.severe("Incompatible dimensions of algebraic operation - vector increment by matrix");
    }

    @Override
    public void increment(MatrixValue value) {
        if (rows != value.rows || cols != value.cols) {
            LOG.severe("Incompatible incrementing of matrix with matrix ");
        }
        double[][] otherValues = value.values;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i][j] += otherValues[i][j];
            }
        }
    }

    /**
     * DDD
     *
     * @param maxValue
     * @return
     */
    @Override
    public boolean greaterThan(Value maxValue) {
        return maxValue.greaterThan(this);
    }

    @Override
    public boolean greaterThan(ScalarValue maxValue) {
        int greater = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (values[i][j] < maxValue.value) {
                    greater++;
                }
            }
        }
        return greater > cols * rows / 2;
    }

    @Override
    public boolean greaterThan(VectorValue maxValue) {
        LOG.severe("Incompatible dimensions of algebraic operation - vector greaterThan matrix");
        return false;
    }

    @Override
    public boolean greaterThan(MatrixValue maxValue) {
        if (rows != maxValue.rows || cols != maxValue.cols) {
            LOG.severe("Matrix element-wise comparison dimension mismatch");
        }
        int greater = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (values[i][j] < maxValue.values[i][j]) {
                    greater++;
                }
            }
        }
        return greater > cols * rows / 2;
    }
}
