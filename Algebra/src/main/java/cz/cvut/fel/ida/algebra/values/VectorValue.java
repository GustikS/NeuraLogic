package cz.cvut.fel.ida.algebra.values;

import com.sun.istack.internal.NotNull;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.setup.Settings;

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

    private VectorValue(double[] values, boolean rowOrientation) {
        this.values = values;
        this.rowOrientation = rowOrientation;
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
        VectorValue result = new VectorValue(values.length);
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(",").append(Settings.shortNumberFormat.format(values[i]));
        }
        sb.replace(0, 1, "[");
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
            LOG.finest("Performing vector x vector matrix multiplication.");
            MatrixValue result = new MatrixValue(value.values.length, values.length);
            double[][] resultValues = result.values;
            for (int i = 0; i < value.values.length; i++) {
                for (int j = 0; j < values.length; j++) {
                    resultValues[i][j] = value.values[i] * values[j];
                }
            }
            return result;
        } else {
            LOG.severe("Incompatible dimensions for vector multiplication: " + Arrays.toString(value.size()) + " x " + Arrays.toString(size()) + " (try transposition)");
            return null;
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
            LOG.severe("Matrix row length mismatch with vector length for multiplication");
        }
        if (value.cols > 1 && rowOrientation) {
            LOG.severe("Multiplying matrix with a row-oriented vector!");
        }
        VectorValue result = new VectorValue(value.rows);
        double[] resultValues = result.values;
        double[][] matrixValues = value.values;
        for (int i = 0; i < value.rows; i++) {
            for (int j = 0; j < value.cols; j++) {
                resultValues[i] += matrixValues[i][j] * this.values[j];
            }
        }
        return result;
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
            LOG.severe("Vector element-wise multiplication dimension mismatch");
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
        LOG.warning("Calculation matrix element-wise product with vector...");
        if (value.cols != values.length) {
            LOG.severe("Matrix row length mismatch with vector length for multiplication");
        }
        MatrixValue result = new MatrixValue(value.rows, value.cols);
        double[][] resultValues = result.values;
        double[][] matrixValues = value.values;
        for (int i = 0; i < value.rows; i++) {
            for (int j = 0; j < value.cols; j++) {
                resultValues[i][j] = matrixValues[i][j] * this.values[j];
            }
        }
        return result;
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
            LOG.severe("Vector element-wise addition dimension mismatch");
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
        LOG.severe("Incompatible summation of matrix plus vector ");
        return null;
    }

    /**
     * Default Double Dispatch
     *
     * @param value
     * @return
     */
    @Override
    public Value minus(Value value) {
        return null;
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
            LOG.severe("Vector element-wise addition dimension mismatch");
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
        LOG.severe("Incompatible dimensions of algebraic operation - matrix minus vector");
        return null;
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
        LOG.severe("Incompatible dimensions of algebraic operation - scalar increment by vector");
    }

    /**
     * DD - switch of sides!!
     *
     * @param value
     */
    @Override
    protected void incrementBy(VectorValue value) {
        if (value.values.length != values.length) {
            LOG.severe("Vector element-wise increment dimension mismatch");
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
        LOG.severe("Incompatible dimensions of algebraic operation - matrix increment by vector");
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
            LOG.severe("Vector element-wise comparison dimension mismatch");
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