package cz.cvut.fel.ida.algebra.values;

import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
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
    public double[] values;

    public MatrixValue(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.values = new double[rows * cols];
    }

    public MatrixValue(double[] values, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.values = values;
    }

    public MatrixValue(List<List<Double>> vectors) {
        this.rows = vectors.size();
        this.cols = vectors.get(0).size();
        this.values = new double[rows * cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i * cols + j] = vectors.get(i).get(j);
            }
        }
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
        int index;

        final int maxIndex = rows * cols;

        @Override
        public boolean hasNext() {
            return index < maxIndex;
        }

        @Override
        public Double next() {
            return values[index++];
        }
    }

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        valueInitializer.initMatrix(this);
    }

    @Override
    public MatrixValue zero() {
        Arrays.fill(values, 0);
        return this;
    }

    @Override
    public MatrixValue clone() {
        return new MatrixValue(values.clone(), rows, cols);
    }

    @Override
    public MatrixValue getForm() {
        return new MatrixValue(rows, cols);
    }

    @Override
    public void transpose() {
        final double[] trValues = new double[values.length];

        for (int i = 0; i < rows; i++) {
            final int tmpIndex = i * cols;

            for (int j = 0; j < cols; j++) {
                trValues[j * rows + i] = values[tmpIndex + j];
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
        final MatrixValue value = new MatrixValue(values, rows, cols);
        value.transpose();

        return value;
    }

    @Override
    public int[] size() {
        return new int[]{rows, cols};
    }

    @Override
    public Value slice(int[] rows, int[] cols) {
        final int rowsTo = rows == null ? this.rows : rows[1];
        final int rowsFrom = rows == null ? 0 : rows[0];

        final int colsTo = cols == null ? this.cols : cols[1];
        final int colsFrom = cols == null ? 0 : cols[0];

        if (rowsFrom < 0 || rowsTo > this.rows || rowsFrom >= rowsTo) {
            String err = "Cannot slice MatrixValue with size " + Arrays.toString(this.size()) + " with row slice " + Arrays.toString(rows);
            LOG.severe(err);
            throw new ArithmeticException(err);
        }

        if (colsFrom < 0 || colsTo > this.cols || colsFrom >= colsTo) {
            String err = "Cannot slice MatrixValue with size " + Arrays.toString(this.size()) + " with col slice " + Arrays.toString(cols);
            LOG.severe(err);
            throw new ArithmeticException(err);
        }

        final int colNum = colsTo - colsFrom;
        final int rowNum = rowsTo - rowsFrom;

        final double[] resValues = new double[rowNum * colNum];

        for (int i = rowsFrom; i < rowsTo; i++) {
            System.arraycopy(values, i * this.cols + colsFrom, resValues, (i - rowsFrom) * colNum, colNum);
        }

        return new MatrixValue(resValues, rowNum, colNum);
    }

    @Override
    public Value reshape(int[] shape) {
        final double[] data = values;

        if (values.length == 1 && shape.length == 1 && shape[0] == 0) {
            return new ScalarValue(values[0]);
        }

        if (shape.length == 1 && shape[0] == data.length) {
            return new VectorValue(data);
        }

        if (shape.length == 2) {
            if (shape[0] == 1 && shape[1] == data.length) {
                return new MatrixValue(data, 1, data.length);
            }

            if (shape[0] == data.length && shape[1] == 1) {
                return new MatrixValue(data, data.length, 1);
            }

            if (shape[0] == 0 && shape[1] == 0 && data.length == 1) {
                return new ScalarValue(data[0]);
            }

            if (shape[0] == 0 && shape[1] == data.length) {
                return new VectorValue(data);
            }

            if (shape[0] == data.length && shape[1] == 0) {
                return new VectorValue(data, true);
            }

            if (data.length / shape[1] == shape[0]) {
                return new MatrixValue(data, shape[0], shape[1]);
            }
        }

        String err = "Cannot reshape MatrixValue of shape " + Arrays.toString(this.size()) + " to shape " + Arrays.toString(shape);
        LOG.severe(err);
        throw new ArithmeticException(err);
    }

    @Override
    public double[] getAsArray() {
        return values;
    }

    @Override
    public void setAsArray(double[] value) {
        this.values = value;
    }

    @Override
    public Value apply(DoubleUnaryOperator function) {
        final MatrixValue result = new MatrixValue(rows, cols);
        final double[] tmpValues = result.values;

        for (int i = 0; i < tmpValues.length; i++) {
            tmpValues[i] = function.applyAsDouble(values[i]);
        }

        return result;
    }

    @Override
    public void applyInplace(DoubleUnaryOperator function) {
        for (int i = 0; i < values.length; i++) {
            values[i] = function.applyAsDouble(values[i]);
        }
    }

    @Override
    public double get(int i) {
        return values[i];
    }

    @Override
    public void set(int i, double value) {
        values[i] = value;
    }

    @Override
    public void increment(int i, double value) {
        values[i] += value;
    }


    @Override
    public String toString(NumberFormat numberFormat) {
        if (numberFormat == null) {
            return "dim:" + Arrays.toString(size());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int j = 0; j < rows; j++) {
            sb.append("[");
            for (int i = 0; i < cols; i++) {
                sb.append(numberFormat.format(values[j * cols + i])).append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), "],\n");
        }
        sb.replace(sb.length() - 2, sb.length(), "\n]");
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
        final double value1 = value.value;
        final double[] values = clone.values;

        for (int i = 0; i < values.length; i++) {
            values[i] *= value1;
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

        final VectorValue result = new VectorValue(cols, true);
        final double[] resultValues = result.values;
        final double[] origValues = value.values;

        for (int j = 0; j < rows; j++) {
            final int tmpIndex = j * cols;
            final double originalValue = origValues[j];

            for (int i = 0; i < cols; i++) {
                resultValues[i] += originalValue * values[tmpIndex + i];
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

        final MatrixValue result = new MatrixValue(value.rows, this.cols);
        final double[] lhs = value.values;
        final double[] resultValues = result.values;

        for (int i = 0; i < value.rows; i++) {         // rows from lhs
            final int tmpIndex = i * cols;
            final int tmpIndexLhs = i * value.cols;

            for (int j = 0; j < cols; j++) {     // columns from rhs
                double acc = resultValues[tmpIndex + j];

                for (int k = 0; k < value.cols; k++) { // columns from lhs
                    acc += lhs[tmpIndexLhs + k] * values[k * cols + j];
                }

                resultValues[tmpIndex + j] = acc;
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
        final MatrixValue clone = this.clone();
        final double value1 = value.value;
        final double[] values = clone.values;

        for (int i = 0; i < values.length; i++) {
            values[i] *= value1;
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
        double[] resultValues = result.values;

        for (int i = 0; i < rows; i++) {
            final int tmpIndex = i * cols;

            for (int j = 0; j < cols; j++) {
                resultValues[tmpIndex + j] = values[tmpIndex + j] * value.values[j];
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

        final MatrixValue result = value.clone();
        final double[] lhs = result.values;
        for (int i = 0; i < lhs.length; i++) {
            lhs[i] *= values[i];
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
        final MatrixValue clone = this.clone();
        final double value1 = value.value;
        final double[] values = clone.values;

        for (int i = 0; i < values.length; i++) {
            values[i] *= value1;
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
        VectorValue result = new VectorValue(cols, true);
        double[] resultValues = result.values;
        double[] origValues = value.values;

        for (int j = 0; j < rows; j++) {
            final int tmpIndex = j * cols;
            final double originalValue = origValues[j];

            for (int i = 0; i < cols; i++) {
                resultValues[i] += originalValue * values[tmpIndex + i];
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

        final MatrixValue result = new MatrixValue(value.cols, this.cols);
        final double[] lhs = value.values;
        final double[] resultValues = result.values;

        for (int i = 0; i < value.cols; i++) {         // rows from transposed lhs
            final int tmpIndex = i * cols;

            for (int k = 0; k < value.rows; k++) { // columns from transposed lhs
                final int valuesTmpIndex = k * cols;
                final double lhsValue = lhs[k * value.cols + i];

                for (int j = 0; j < cols; j++) {     // columns from rhs
                    resultValues[tmpIndex + j] += lhsValue * values[valuesTmpIndex + j];
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
        final MatrixValue clone = this.clone();
        final double[] values = clone.values;

        for (int i = 0; i < values.length; i++) {
            values[i] *= value.value;
        }

        return clone;
    }

    @Override
    protected Value kroneckerTimes(VectorValue vectorValue) {
        final int rows = vectorValue.rows() * this.rows;
        final int cols = vectorValue.cols() * this.cols;

        final MatrixValue result = new MatrixValue(rows, cols);
        final double[] resultValues = result.values;
        final double[] otherValues = vectorValue.values;

        if (vectorValue.rowOrientation) {
            for (int c1 = 0; c1 < otherValues.length; c1++) {
                for (int r2 = 0; r2 < this.rows; r2++) {
                    for (int c2 = 0; c2 < this.cols; c2++) {
                        resultValues[r2 * cols + c1 * this.cols + c2] = otherValues[c1] * values[r2 * this.cols + c2];
                    }
                }
            }
        } else {
            for (int r1 = 0; r1 < otherValues.length; r1++) {
                for (int r2 = 0; r2 < this.rows; r2++) {
                    for (int c2 = 0; c2 < this.cols; c2++) {
                        resultValues[(r1 * this.rows + r2) * cols + c2] = otherValues[r1] * values[r2 * this.cols + c2];
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected Value kroneckerTimes(MatrixValue otherValue) {
        final int rows = this.rows * otherValue.rows;
        final int cols = this.cols * otherValue.cols;

        final MatrixValue result = new MatrixValue(rows, cols);
        final double[] resultValues = result.values;
        final double[] otherValues = otherValue.values;

        for (int r1 = 0; r1 < otherValue.rows; r1++) {
            for (int c1 = 0; c1 < otherValue.cols; c1++) {
                final int otherTmpIndex = r1 * otherValue.cols + c1;

                for (int r2 = 0; r2 < this.rows; r2++) {
                    final int tmpIndex = (r1 * this.rows + r2) * cols + c1 * this.cols;

                    for (int c2 = 0; c2 < this.cols; c2++) {
                        resultValues[tmpIndex + c2] = otherValues[otherTmpIndex] * values[r2 * this.cols + c2];
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
        final MatrixValue clone = this.clone();
        final double value1 = value.value;
        final double[] values = clone.values;

        for (int i = 0; i < values.length; i++) {
            values[i] /= value1;
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

        final MatrixValue result = new MatrixValue(rows, cols);
        final double[] resultValues = result.values;

        for (int i = 0; i < resultValues.length; i++) {
            resultValues[i] = value.values[i % cols] / values[i];
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

        final MatrixValue result = value.clone();
        final double[] lhs = result.values;

        for (int i = 0; i < lhs.length; i++) {
            lhs[i] /= values[i];
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
        final MatrixValue clone = clone();
        final double value1 = value.value;
        final double[] values = clone.values;

        for (int i = 0; i < values.length; i++) {
            values[i] += value1;
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

        final MatrixValue result = new MatrixValue(rows, cols);
        final double[] resultValues = result.values;
        final double[] otherValues = value.values;

        for (int i = 0; i < resultValues.length; i++) {
            resultValues[i] = otherValues[i] + values[i];
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
        final MatrixValue result = new MatrixValue(rows, cols);
        final double[] resultValues = result.values;
        final double value1 = value.value;

        for (int i = 0; i < resultValues.length; i++) {
            resultValues[i] = value1 - values[i];
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

        final MatrixValue result = new MatrixValue(rows, cols);
        final double[] resultValues = result.values;
        final double[] otherValues = value.values;

        for (int i = 0; i < resultValues.length; i++) {
            resultValues[i] = otherValues[i] - values[i];
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
        if (rows == 1 && cols == 1) {
            value.value += values[0];
        } else {
            String err = "Incompatible dimensions of algebraic operation - scalar incrementBy by matrix";
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
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

        double[] otherValues = value.values;
        for (int i = 0; i < otherValues.length; i++) {
            otherValues[i] += values[i];
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

        double[] otherValues = value.values;

        for (int i = 0; i < otherValues.length; i++) {
            otherValues[i] *= values[i];
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
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return maxValue.value > sum;
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
        double thisSum = 0;
        double otherSum = 0;
        for (int i = 0; i < values.length; i++) {
            thisSum += values[i];
            otherSum += maxValue.values[i];
        }
        return otherSum > thisSum;
    }

    @Override
    protected boolean greaterThan(TensorValue maxValue) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Matrix are not implemented yet");
    }


    @Override
    public int hashCode() {
        long hashCode = 1;
        for (int i = 0; i < values.length; i++)
            hashCode = 31 * hashCode + Double.valueOf(values[i]).hashCode();
        return Long.hashCode(hashCode);
    }


    @Override
    public boolean equals(Value obj) {
        if (obj instanceof MatrixValue) {
            MatrixValue m = (MatrixValue) obj;

            if (m.cols != cols || m.rows != rows) {
                return false;
            }

            return Arrays.equals(values, m.values);
        }
        return false;
    }
}
