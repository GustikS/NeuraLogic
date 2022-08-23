package cz.cvut.fel.ida.algebra.values;

import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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

    public MatrixValue(List<List<Double>> vectors) {
        this.rows = vectors.size();
        this.cols = vectors.get(0).size();
        this.values = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i][j] = vectors.get(i).get(j);
            }
        }
    }

    public MatrixValue(double[][] values) {
        this.values = values;
        this.rows = values.length;
        this.cols = values[0].length;
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
            return row <= maxRow;
        }

        @Override
        public Double next() {
            double next = values[row][col];
            if (col < maxCol)
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
    public void transpose() {

        double[][] trValues = new double[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                trValues[j][i] = values[i][j];
            }
        }

        values = trValues;

        int tmp = rows;
        rows = cols;
        cols = tmp;
    }

    @Override
    public Value transposedView() {
//        LOG.severe("Transposed view of a matrix (without actual transposition) not implemented, returning a transposed copy instead!");
        MatrixValue value = new MatrixValue(values);
        value.transpose();
        return value;
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
    public double get(int i) {
        return values[i / cols][i % cols];
    }

    @Override
    public void set(int i, double value) {
        values[i / cols][i % cols] = value;
    }

    @Override
    public void increment(int i, double value) {
        values[i / cols][i % cols] += value;
    }


    @Override
    public String toString(NumberFormat numberFormat) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int j = 0; j < values.length; j++) {
            sb.append("[");
            for (int i = 0; i < values[j].length; i++) {
                sb.append(numberFormat.format(values[j][i])).append(",");
            }
            sb.replace(sb.length()-1, sb.length(), "],\n");
        }
        sb.replace(sb.length()-2, sb.length(), "\n]");
        return sb.toString();
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
    protected MatrixValue times(ScalarValue value) {
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
    protected VectorValue times(VectorValue value) {
        if (rows != value.values.length) {
            String err = "Matrix row length mismatch with vector length for multiplication: " + rows + " vs." + value.values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        if (!value.rowOrientation) {
            throw new ArithmeticException("Column vector times matrix, try transposition. Vector size = " + value.values.length);
        }
        VectorValue result = new VectorValue(cols,true);
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
    protected MatrixValue times(MatrixValue value) {
        if (value.cols != rows) {
            String err = "Matrix to matrix dimension mismatch for multiplication " + value.cols + " != " + rows;
            LOG.severe(err);
            throw new ArithmeticException(err);
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

    @Override
    protected Value times(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Matrix are not implemented yet");
    }

    @Override
    public Value elementTimes(Value value) {
        return value.elementTimes(this);
    }

    @Override
    protected Value elementTimes(ScalarValue value) {
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
    protected Value elementTimes(VectorValue value) {
        LOG.warning("Calculation vector element-wise product with matrix...");
        if (rows != value.values.length) {
            String err = "Matrix row length mismatch with vector length for multiplication" + rows + " vs." + value.values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        MatrixValue result = new MatrixValue(rows, cols);
        double[][] resultValues = result.values;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultValues[i][j] = values[i][j] * value.values[j];
            }
        }
        return result;
    }

    @Override
    protected Value elementTimes(MatrixValue value) {
        if (value.cols != cols || value.rows != rows) {
            String err = "Matrix elementTimes dimension mismatch: " + Arrays.toString(this.size()) + " vs." + Arrays.toString(value.size());
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        MatrixValue result = value.clone();
        double[][] lhs = result.values;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                lhs[i][j] *= values[i][j];
            }
        }
        return result;
    }

    @Override
    protected Value elementTimes(TensorValue value) {
        throw new ArithmeticException("Algebraic operation between Tensor and Matrix are not implemented yet");
    }

    @Override
    public Value transposedTimes(Value value) {
        return value.transposedTimes(this);
    }

    @Override
    protected Value transposedTimes(ScalarValue value) {
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
    protected Value transposedTimes(VectorValue value) {
        if (rows != value.values.length) {
            String err = "Matrix row length mismatch with vector length for multiplication: " + rows + " vs." + value.values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        if (value.rowOrientation) {
            throw new ArithmeticException("Row vector transposedTimes matrix, try transposition. Vector size = " + value.values.length);
        }
        VectorValue result = new VectorValue(cols,true);
        double[] resultValues = result.values;
        double[] origValues = value.values;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                resultValues[i] += origValues[j] * values[j][i];
            }
        }
        return result;
    }

    @Override
    protected Value transposedTimes(MatrixValue value) {
        if (value.rows != rows) {
            String err = "Matrix to matrix dimension mismatch for transposed multiplication" + value.rows + " != " + rows;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        MatrixValue result = new MatrixValue(value.cols, this.cols);
        double[][] lhs = value.values;

        double[][] resultValues = result.values;
        for (int i = 0; i < value.cols; i++) {         // rows from transposed lhs
            for (int j = 0; j < cols; j++) {     // columns from rhs
                for (int k = 0; k < value.rows; k++) { // columns from transposed lhs
                    resultValues[i][j] += lhs[k][i] * values[k][j];
                }
            }
        }
        return result;
    }

    @Override
    protected Value transposedTimes(TensorValue value) {
        throw new ArithmeticException("Algebraic operation between Tensor and Matrix are not implemented yet");
    }

    @Override
    public Value kroneckerTimes(Value value) {
        return value.kroneckerTimes(this);
    }

    @Override
    protected Value kroneckerTimes(ScalarValue value) {
        MatrixValue clone = this.clone();
        for (int i = 0; i < clone.rows; i++) {
            for (int j = 0; j < clone.cols; j++) {
                clone.values[i][j] *= value.value;
            }
        }
        return clone;
    }

    @Override
    protected Value kroneckerTimes(VectorValue vectorValue) {
        int rows = vectorValue.rows() * this.rows;
        int cols = vectorValue.cols() * this.cols;

        MatrixValue result = new MatrixValue(rows, cols);
        double[][] resultValues = result.values;
        double[] otherValues = vectorValue.values;
        if (vectorValue.rowOrientation) {
            for (int c1 = 0; c1 < otherValues.length; c1++) {
                for (int r2 = 0; r2 < this.rows; r2++) {
                    for (int c2 = 0; c2 < this.cols; c2++) {
                        resultValues[r2][c1 * this.cols + c2] = otherValues[c1] * values[r2][c2];
                    }
                }
            }
        } else {
            for (int r1 = 0; r1 < otherValues.length; r1++) {
                for (int r2 = 0; r2 < this.rows; r2++) {
                    for (int c2 = 0; c2 < this.cols; c2++) {
                        resultValues[r1 * this.rows + r2][c2] = otherValues[r1] * values[r2][c2];
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected Value kroneckerTimes(MatrixValue otherValue) {
        int rows = this.rows * otherValue.rows;
        int cols = this.cols * otherValue.cols;

        MatrixValue result = new MatrixValue(rows, cols);
        double[][] resultValues = result.values;
        double[][] otherValues = otherValue.values;

        for (int r1 = 0; r1 < otherValue.rows; r1++) {
            for (int c1 = 0; c1 < otherValue.cols; c1++) {
                for (int r2 = 0; r2 < this.rows; r2++) {
                    for (int c2 = 0; c2 < this.cols; c2++) {
                        resultValues[r1 * this.rows + r2][c1 * this.cols + c2] = otherValues[r1][c1] * values[r2][c2];
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected Value kroneckerTimes(TensorValue value) {
        throw new ArithmeticException("Algebraic operation between Tensor and Matrix are not implemented yet");
    }

    @Override
    public Value elementDivideBy(Value value) {
        return value.elementDivideBy(this);
    }

    @Override
    protected Value elementDivideBy(ScalarValue value) {
        MatrixValue clone = this.clone();
        double value1 = value.value;
        for (int i = 0; i < clone.rows; i++) {
            for (int j = 0; j < clone.cols; j++) {
                clone.values[i][j] /= value1;
            }
        }
        return clone;
    }

    @Override
    protected Value elementDivideBy(VectorValue value) {
        LOG.warning("Calculation vector element-wise division with matrix...");
        if (rows != value.values.length) {
            String err = "Matrix row length mismatch with vector length for multiplication" + rows + " vs." + value.values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        MatrixValue result = new MatrixValue(rows, cols);
        double[][] resultValues = result.values;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultValues[i][j] = value.values[j] / values[i][j];
            }
        }
        return result;
    }

    @Override
    protected Value elementDivideBy(MatrixValue value) {
        if (value.cols != cols || value.rows != rows) {
            String err = "Matrix elementTimes dimension mismatch: " + Arrays.toString(this.size()) + " vs." + Arrays.toString(value.size());
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        MatrixValue result = value.clone();
        double[][] lhs = result.values;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                lhs[i][j] /= values[i][j];
            }
        }
        return result;
    }

    @Override
    protected Value elementDivideBy(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Matrix are not implemented yet");
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
    protected MatrixValue plus(ScalarValue value) {
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
    protected Value plus(VectorValue value) {
        throw new ArithmeticException("Incompatible summation of matrix plus vector ");
    }

    @Override
    protected Value plus(MatrixValue value) {
        if (rows != value.rows || cols != value.cols) {
            String err = "Matrix plus dimension mismatch: " + Arrays.toString(this.size()) + " vs." + Arrays.toString(value.size());
            LOG.severe(err);
            throw new ArithmeticException(err);
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

    @Override
    protected Value plus(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Matrix are not implemented yet");
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
    protected Value minus(ScalarValue value) {
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
    protected Value minus(VectorValue value) {
        LOG.severe("Incompatible dimensions of algebraic operation - vector minus matrix");
        return null;
    }

    @Override
    protected Value minus(MatrixValue value) {
        if (rows != value.rows || cols != value.cols) {
            String err = "Matrix minus dimension mismatch: " + Arrays.toString(this.size()) + " vs." + Arrays.toString(value.size());
            LOG.severe(err);
            throw new ArithmeticException(err);
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

    @Override
    protected Value minus(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Matrix are not implemented yet");
    }

    /**
     * DDD
     *
     * @param value
     */
    @Override
    public void incrementBy(Value value) {
        value.incrementBy(this);
    }

    /**
     * DD - switch of sides!!
     *
     * @param value
     */
    @Override
    protected void incrementBy(ScalarValue value) {
        String err = "Incompatible dimensions of algebraic operation - scalar incrementBy by matrix";
        LOG.severe(err);
        throw new ArithmeticException(err);
    }

    /**
     * DD - switch of sides!!
     *
     * @param value
     */
    @Override
    protected void incrementBy(VectorValue value) {
        String err = "Incompatible dimensions of algebraic operation - vector incrementBy by matrix";
        LOG.severe(err);
        throw new ArithmeticException(err);
    }

    /**
     * DD - switch of sides!!
     *
     * @param value
     */
    @Override
    protected void incrementBy(MatrixValue value) {
        if (rows != value.rows || cols != value.cols) {
            String err = "Matrix incrementBy dimension mismatch: " + Arrays.toString(this.size()) + " vs." + Arrays.toString(value.size());
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        double[][] otherValues = value.values;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                otherValues[i][j] += values[i][j];
            }
        }
    }

    @Override
    protected void incrementBy(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Matrix are not implemented yet");
    }

    @Override
    public void elementMultiplyBy(Value value) {
        value.elementMultiplyBy(this);
    }

    @Override
    protected void elementMultiplyBy(ScalarValue value) {
        String err = "Incompatible dimensions of algebraic operation - scalar multiplyBy by matrix";
        LOG.severe(err);
        throw new ArithmeticException(err);
    }

    @Override
    protected void elementMultiplyBy(VectorValue value) {
        String err = "Incompatible dimensions of algebraic operation - vector multiplyBy by matrix";
        LOG.severe(err);
        throw new ArithmeticException(err);
    }

    @Override
    protected void elementMultiplyBy(MatrixValue value) {
        if (rows != value.rows || cols != value.cols) {
            String err = "Matrix multiplyBy dimension mismatch: " + Arrays.toString(this.size()) + " vs." + Arrays.toString(value.size());
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        double[][] otherValues = value.values;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                otherValues[i][j] *= values[i][j];
            }
        }
    }

    @Override
    protected void elementMultiplyBy(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Matrix are not implemented yet");
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
    protected boolean greaterThan(ScalarValue maxValue) {
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
    protected boolean greaterThan(VectorValue maxValue) {
        LOG.severe("Incompatible dimensions of algebraic operation - vector greaterThan matrix");
        return false;
    }

    @Override
    protected boolean greaterThan(MatrixValue maxValue) {
        if (rows != maxValue.rows || cols != maxValue.cols) {
            String err = "Matrix greaterThan dimension mismatch: " + Arrays.toString(this.size()) + " vs." + Arrays.toString(maxValue.size());
            LOG.severe(err);
            throw new ArithmeticException(err);
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

    @Override
    protected boolean greaterThan(TensorValue maxValue) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Matrix are not implemented yet");
    }

    @Override
    public boolean equals(Value obj) {
        if (obj instanceof MatrixValue) {
            if (Arrays.deepEquals(values, ((MatrixValue) obj).values)) {
                return true;
            }
        }
        return false;
    }
}
