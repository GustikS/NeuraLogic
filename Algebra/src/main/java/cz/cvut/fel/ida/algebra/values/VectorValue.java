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
 * We consider vectors as both column and row vectors, default is column orientation.
 * Created by gusta on 8.3.17.
 */
public class VectorValue extends Value {
    private static final Logger LOG = Logger.getLogger(VectorValue.class.getName());
    /**
     * The actual vector of values
     */
    public double[] values;

    /**
     * Information about orientation/transposition
     */
    public boolean rowOrientation = false;

    public VectorValue(int size) {
        values = new double[size];
    }

    public VectorValue(List<Double> vector) {
        values = vector.stream().mapToDouble(d -> d).toArray();
    }

    public VectorValue(int size, ValueInitializer valueInitializer) {
        values = new double[size];
        initialize(valueInitializer);
    }

    public VectorValue(double[] values) {
        this.values = values;
    }

    public VectorValue(double[] values, boolean rowOrientation) {
        this.values = values;
        this.rowOrientation = rowOrientation;
    }

    public VectorValue(int size, boolean rowOrientation) {
        values = new double[size];
        this.rowOrientation = rowOrientation;
    }


    protected int rows() {
        if (rowOrientation) {
            return 1;
        } else {
            return values.length;
        }
    }

    protected int cols() {
        if (!rowOrientation) {
            return 1;
        } else {
            return values.length;
        }
    }

    @NotNull
    @Override
    public Iterator<Double> iterator() {
        return new ValueIterator();
    }

    protected class ValueIterator implements Iterator<Double> {
        int i = 0;

        @Override
        public boolean hasNext() {
            return i < values.length;
        }

        @Override
        public Double next() {
            return values[i++];
        }
    }

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        valueInitializer.initVector(this);
    }

    @Override
    public VectorValue zero() {
        for (int i = 0; i < values.length; i++) {
            values[i] = 0;
        }
        return this;
    }

    @Override
    public VectorValue clone() {
        VectorValue clone = new VectorValue(values.length);
        clone.rowOrientation = rowOrientation;
        for (int i = 0; i < clone.values.length; i++) {
            clone.values[i] = this.values[i];
        }
        return clone;
    }

    @Override
    public VectorValue getForm() {
        VectorValue form = new VectorValue(values.length);
        form.rowOrientation = rowOrientation;
        return form;
    }

    @Override
    public void transpose() {
        rowOrientation = !rowOrientation;
    }

    @Override
    public Value transposedView() {
        return new VectorValue(values, !rowOrientation);
    }

    @Override
    public int[] size() {
        if (rowOrientation) {
            return new int[]{1, values.length};
        } else {
            return new int[]{values.length, 1};
        }
    }

    @Override
    public Value apply(Function<Double, Double> function) {
        VectorValue result = new VectorValue(values.length, rowOrientation);
        double[] resultValues = result.values;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = function.apply(values[i]);
        }
        return result;
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(",").append(numberFormat.format(values[i]));
        }
        sb.replace(0, 1, "[");
        sb.append("]");
        return sb.toString();
    }

    /**
     * Default Double Dispatch
     *
     * @param value
     * @return
     */
    @Override
    public Value times(Value value) {
        return value.times(this);
    }

    @Override
    protected VectorValue times(ScalarValue value) {
        VectorValue result = this.getForm();
        double[] resultValues = result.values;
        double otherValue = value.value;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = values[i] * otherValue;
        }
        return result;
    }

    /**
     * Dot product vs matrix multiplication depending on orientation of the vectors
     *
     * @param value
     * @return
     */
    @Override
    protected Value times(VectorValue value) {
        if (value.rowOrientation && !this.rowOrientation && value.values.length == values.length) {
            ScalarValue result = new ScalarValue(0);
            double resultValue = 0;
            double[] otherValues = value.values;
            for (int i = 0; i < values.length; i++) {
                resultValue += values[i] * otherValues[i];
            }
            result.value = resultValue;
            return result;
        } else if (!value.rowOrientation && this.rowOrientation) {
            LOG.finest(() -> "Performing vector x vector matrix multiplication.");
            final MatrixValue result = new MatrixValue(value.values.length, values.length);
            final double[] resultValues = result.values;

            for (int i = 0; i < value.values.length; i++) {
                final int tmpValue = i * values.length;

                for (int j = 0; j < values.length; j++) {
                    resultValues[tmpValue + j] = value.values[i] * values[j];
                }
            }
            return result;
        } else {
            String err = "Incompatible dimensions for vector multiplication: " + Arrays.toString(value.size()) + " vs " + Arrays.toString(this.size()) + " (try transposition)";
            LOG.severe(err);
            throw new ArithmeticException(err); // todo measure if any cost of this
//            return null;
        }
    }

    /**
     * Vectors are by default taken as columns, so multiplication is against the rows of the matrix.
     * <p>
     * The result is naturally a vector of size rows(Matrix)
     *
     * @param value
     * @return
     */
    @Override
    protected VectorValue times(MatrixValue value) {
        if (value.cols != values.length) {
            String err = "Matrix row length mismatch with vector length for multiplication: " + value.cols + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        if (value.cols > 1 && rowOrientation) {
            LOG.severe("Multiplying matrix with a row-oriented vector: " + Arrays.toString(value.size()) + " times " + Arrays.toString(this.size()));
            throw new ArithmeticException("Multiplying matrix with a row-oriented vector: " + Arrays.toString(value.size()) + " times " + Arrays.toString(this.size()));
        }

        final VectorValue result = new VectorValue(value.rows);
        final double[] resultValues = result.values;
        final double[] matrixValues = value.values;

        for (int i = 0; i < value.rows; i++) {
            final int tmpIndex = i * value.cols;

            for (int j = 0; j < value.cols; j++) {
                resultValues[i] += matrixValues[tmpIndex + j] * this.values[j];
            }
        }
        return result;
    }

    @Override
    protected Value times(TensorValue value) {
        throw new ArithmeticException("Algebraic operation between Tensor and Vector are not implemented yet");
    }

    @Override
    public Value elementTimes(Value value) {
        return value.elementTimes(this);
    }

    @Override
    protected Value elementTimes(ScalarValue value) {
        VectorValue result = this.getForm();
        double[] resultValues = result.values;
        double otherValue = value.value;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = values[i] * otherValue;
        }
        return result;
    }

    @Override
    protected Value elementTimes(VectorValue value) {
        if (value.values.length != values.length) {
            String err = "Vector elementTimes dimension mismatch: " + value.values.length + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        VectorValue result = value.getForm();
        double[] resultValues = result.values;
        double[] otherValues = value.values;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = values[i] * otherValues[i];
        }
        return result;
    }

    @Override
    protected Value elementTimes(MatrixValue value) {
        LOG.warning("Calculating matrix element-wise product with vector...");
        if (value.cols != values.length) {
            String err = "Matrix elementTimes vector broadcast dimension mismatch: " + value.cols + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        MatrixValue result = new MatrixValue(value.rows, value.cols);
        double[] resultValues = result.values;
        double[] matrixValues = value.values;

        for (int i = 0; i < resultValues.length; i++) {
            resultValues[i] = matrixValues[i] * this.values[i % value.cols];
        }

        return result;
    }

    @Override
    protected Value elementTimes(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Vector are not implemented yet");
    }

    @Override
    public Value kroneckerTimes(Value value) {
        return value.kroneckerTimes(this);
    }

    @Override
    protected Value kroneckerTimes(ScalarValue value) {
        VectorValue result = this.getForm();
        double[] resultValues = result.values;
        double otherValue = value.value;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = values[i] * otherValue;
        }
        return result;
    }

    @Override
    protected Value kroneckerTimes(VectorValue value) {
        int rows = rows() * value.rows();
        int cols = cols() * value.cols();

        if (rows == 1 || cols == 1) {
            VectorValue result = new VectorValue(rows * cols, rows == 1);
            double[] resultValues = result.values;
            double[] otherValues = value.values;
            for (int i = 0; i < otherValues.length; i++) {
                for (int j = 0; j < values.length; j++) {
                    resultValues[i * values.length + j] = otherValues[i] * values[j];
                }
            }
            return result;
        } else {
            MatrixValue result = new MatrixValue(rows, cols);
            final double[] resultValues = result.values;
            final double[] otherValues = value.values;

            if (rowOrientation) {
                for (int i = 0; i < otherValues.length; i++) {
                    final int tmpIndex = i * values.length;

                    for (int j = 0; j < values.length; j++) {
                        resultValues[tmpIndex + j] = otherValues[i] * values[j];
                    }
                }
            } else {
                for (int j = 0; j < values.length; j++) {
                    final int tmpIndex = j * otherValues.length;

                    for (int i = 0; i < otherValues.length; i++) {
                        resultValues[tmpIndex + i] = otherValues[i] * values[j];
                    }
                }
            }
            return result;
        }
    }

    @Override
    protected Value kroneckerTimes(MatrixValue matrix) {
        int rows = rows() * matrix.rows;
        int cols = cols() * matrix.cols;

        MatrixValue result = new MatrixValue(rows, cols);
        double[] resultValues = result.values;
        double[] otherValues = matrix.values;

        if (rowOrientation) {
            for (int r1 = 0; r1 < matrix.rows; r1++) {
                for (int c1 = 0; c1 < matrix.cols; c1++) {
                    for (int k = 0; k < values.length; k++) {
                        final int tmpIndex = c1 * values.length + k;

                        resultValues[r1 *  cols + tmpIndex] = otherValues[r1 * matrix.cols + c1] * values[k];
                    }
                }
            }
        } else {
            for (int r1 = 0; r1 < matrix.rows; r1++) {
                for (int c1 = 0; c1 < matrix.cols; c1++) {
                    for (int k = 0; k < values.length; k++) {
                        final int tmpIndex = r1 * values.length + k;
                        resultValues[tmpIndex * cols + c1] = otherValues[r1 * matrix.cols + c1] * values[k];
                    }
                }
            }
        }
        return result;

    }

    @Override
    protected Value kroneckerTimes(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Vector are not implemented yet");
    }

    @Override
    public Value elementDivideBy(Value value) {
        return value.elementDivideBy(this);
    }

    @Override
    protected Value elementDivideBy(ScalarValue value) {
        VectorValue result = this.getForm();
        double[] resultValues = result.values;
        double otherValue = value.value;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = otherValue / values[i];
        }
        return result;
    }

    @Override
    protected Value elementDivideBy(VectorValue value) {
        if (value.values.length != values.length) {
            String err = "Vector elementTimes dimension mismatch: " + value.values.length + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        VectorValue result = value.getForm();
        double[] resultValues = result.values;
        double[] otherValues = value.values;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = otherValues[i] / values[i];
        }
        return result;
    }

    @Override
    protected Value elementDivideBy(MatrixValue value) {
        LOG.warning("Calculation matrix element-wise product with vector...");
        if (value.cols != values.length) {
            String err = "Matrix elementTimes vector broadcast dimension mismatch: " + value.cols + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        MatrixValue result = new MatrixValue(value.rows, value.cols);
        double[] resultValues = result.values;
        double[] matrixValues = value.values;

        for (int i = 0; i < value.rows; i++) {
            final int tmpIndex = i * value.cols;

            for (int j = 0; j < value.cols; j++) {
                resultValues[tmpIndex + j] = matrixValues[tmpIndex + j] / values[j];
            }
        }
        return result;
    }

    @Override
    protected Value elementDivideBy(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Vector are not implemented yet");
    }

    /**
     * Default double dispatch
     *
     * @param value
     * @return
     */
    @Override
    public Value plus(Value value) {
        return value.plus(this);
    }

    @Override
    protected VectorValue plus(ScalarValue value) {
        double other = value.value;
        VectorValue result = this.getForm();
        double[] resultValues = result.values;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = values[i] + other;
        }
        return result;
    }

    @Override
    protected VectorValue plus(VectorValue value) {
        if (value.values.length != values.length) {
            String err = "Vector element plus dimension mismatch: " + value.values.length + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        VectorValue result = value.getForm();
        double[] resultValues = result.values;
        double[] otherValues = value.values;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = values[i] + otherValues[i];
        }
        return result;
    }

    /**
     * This is just not allowed unless the matrix is degenerated to vector, but it is just safer to disallow.
     *
     * @param value
     * @return
     */
    @Override
    protected Value plus(MatrixValue value) {
        throw new ArithmeticException("Incompatible summation of matrix plus vector ");
    }

    @Override
    protected Value plus(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Vector are not implemented yet");
    }

    /**
     * Default Double Dispatch
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
        double other = value.value;
        VectorValue result = this.getForm();
        double[] resultValues = result.values;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = other - values[i];
        }
        return result;
    }

    @Override
    protected Value minus(VectorValue value) {
        if (value.values.length != values.length) {
            String err = "Vector minus dimension mismatch: " + value.values.length + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        VectorValue result = value.getForm();
        double[] resultValues = result.values;
        double[] otherValues = value.values;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = otherValues[i] - values[i];
        }
        return result;
    }

    @Override
    protected Value minus(MatrixValue value) {
        throw new ArithmeticException("Incompatible dimensions of algebraic operation - matrix minus vector");
    }

    @Override
    protected Value minus(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Vector are not implemented yet");
    }

    /**
     * Default double dispatch
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
        throw new ArithmeticException("Incompatible dimensions of algebraic operation - scalar increment by vector");
    }

    /**
     * DD - switch of sides!!
     *
     * @param value
     */
    @Override
    protected void incrementBy(VectorValue value) {
        if (value.values.length != values.length) {
            String err = "Vector incrementBy dimension mismatch: " + value.values.length + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        double[] otherValues = value.values;
        for (int i = 0; i < otherValues.length; i++) {
            otherValues[i] += values[i];
        }
    }

    /**
     * DD - switch of sides!!
     *
     * @param value
     */
    @Override
    protected void incrementBy(MatrixValue value) {
        String err = "Incompatible dimensions of algebraic operation - matrix increment by vector";
        LOG.severe(err);
        throw new ArithmeticException(err);

    }

    @Override
    protected void incrementBy(TensorValue value) {
        throw new ArithmeticException("Algebraic operation between Tensor and Vector are not implemented yet");
    }

    @Override
    public void elementMultiplyBy(Value value) {
        value.elementMultiplyBy(this);
    }

    @Override
    protected void elementMultiplyBy(ScalarValue value) {
        String err = "Incompatible dimensions of algebraic operation - scalar multiplyBy by vector";
        LOG.severe(err);
        throw new ArithmeticException(err);
    }

    @Override
    protected void elementMultiplyBy(VectorValue value) {
        if (value.values.length != values.length) {
            String err = "Vector multiplyBy dimension mismatch: " + value.values.length + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        double[] otherValues = value.values;
        for (int i = 0; i < otherValues.length; i++) {
            otherValues[i] *= values[i];
        }
    }

    @Override
    protected void elementMultiplyBy(MatrixValue value) {
        String err = "Incompatible dimensions of algebraic operation - matrix multiplyBy by vector";
        LOG.severe(err);
        throw new ArithmeticException(err);
    }

    @Override
    protected void elementMultiplyBy(TensorValue value) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Vector are not implemented yet");
    }

    /**
     * Default double dispatch
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
        for (int i = 0; i < values.length; i++) {
            if (maxValue.value > values[i]) {
                greater++;
            }
        }
        return greater > values.length / 2;
    }

    @Override
    protected boolean greaterThan(VectorValue maxValue) {
        if (maxValue.values.length != values.length) {
            String err = "Vector greaterThan dimension mismatch: " + maxValue.values.length + " vs." + values.length;
            LOG.severe(err);
            throw new ArithmeticException(err);
        }
        int greater = 0;
        for (int i = 0; i < values.length; i++) {
            if (maxValue.values[i] > values[i]) {
                greater++;
            }
        }
        return greater > values.length / 2;
    }

    @Override
    protected boolean greaterThan(MatrixValue maxValue) {
        LOG.severe("Incompatible dimensions of algebraic operation - matrix greaterThan vector");
        return false;
    }

    @Override
    protected boolean greaterThan(TensorValue maxValue) {
        throw new ArithmeticException("Algebbraic operation between Tensor and Vector are not implemented yet");
    }

    @Override
    public boolean equals(Value obj) {
        if (obj instanceof VectorValue) {
            if (Arrays.equals(values, ((VectorValue) obj).values)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public int hashCode() {
        long hashCode = 1;
        for (int i = 0; i < values.length; i++)
            hashCode = 31 * hashCode + Double.valueOf(values[i]).hashCode();
        return Long.hashCode(hashCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VectorValue)) {
            return false;
        }
        VectorValue vectorValue = (VectorValue) obj;
        if (vectorValue.values.length != values.length)
            return false;

        for (int i = 0; i < values.length; i++) {
            if (values[i] != vectorValue.values[i])
                return false;
        }
        return true;
    }
}