package networks.computation.evaluation.values;

import networks.computation.evaluation.values.distributions.ValueInitializer;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * We consider vectors as both column and row vectors so that the multiplication with a matrix can return a vector.
 *
 * Created by gusta on 8.3.17.
 */
public class VectorValue extends Value {
    private static final Logger LOG = Logger.getLogger(VectorValue.class.getName());
    /**
     * The actual vector of values
     */
    public double[] values;

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
        for (int i = 0; i < clone.values.length; i++) {
            clone.values[i] = this.values[i];
        }
        return clone;
    }

    @Override
    public VectorValue getForm() {
        return new VectorValue(values.length);
    }

    @Override
    public int[] size() {
        return new int[]{values.length};
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

    /**
     * Default Double Dispatch
     * @param value
     * @return
     */
    @Override
    public Value times(Value value) {
        return value.times(this);
    }

    @Override
    public VectorValue times(ScalarValue value) {
        VectorValue result = this.getForm();
        double[] resultValues = result.values;
        double otherValue = value.value;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = values[i] * otherValue;
        }
        return result;
    }

    //todo take care of an element-wise multiplication vs matrix
    /**
     * ELEMENT-WISE multiplication
     * @param value
     * @return
     */
    @Override
    public Value times(VectorValue value) {
        if (value.values.length != values.length){
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

    /**
     * Vectors are by default taken as columns, so multiplication is against the rows of the matrix.
     *
     * The result is naturally a vector of size rows(Matrix)
     * @param value
     * @return
     */
    @Override
    public VectorValue times(MatrixValue value) {
        if (value.cols != values.length){
            LOG.severe("Matrix row length mismatch with vector length for multiplication");
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

    /**
     * Default double dispatch
     * @param value
     * @return
     */
    @Override
    public Value plus(Value value) {
        return null;
    }

    @Override
    public VectorValue plus(ScalarValue value) {
        double other = value.value;
        VectorValue result = this.getForm();
        double[] resultValues = result.values;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = values[i] + other;
        }
        return result;
    }

    @Override
    public VectorValue plus(VectorValue value) {
        if (value.values.length != values.length){
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
     * @param value
     * @return
     */
    @Override
    public Value plus(MatrixValue value) {
        LOG.severe("Incompatible summation of matrix plus vector ");
        return null;
    }

    /**
     * Default Double Dispatch
     * @param value
     * @return
     */
    @Override
    public Value minus(Value value) {
        return null;
    }

    @Override
    public Value minus(ScalarValue value) {
        double other = value.value;
        VectorValue result = this.getForm();
        double[] resultValues = result.values;
        for (int i = 0; i < values.length; i++) {
            resultValues[i] = other - values[i];
        }
        return result;
    }

    @Override
    public Value minus(VectorValue value) {
        if (value.values.length != values.length){
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
    public Value minus(MatrixValue value) {
        LOG.severe("Incompatible dimensions of algebraic operation - matrix minus vector");
        return null;
    }

    /**
     * Default double dispatch
     * @param value
     */
    @Override
    public void increment(Value value) {
        value.increment(this);
    }

    @Override
    public void increment(ScalarValue value) {
        LOG.severe("Incompatible dimensions of algebraic operation - scalar increment by vector");
    }

    @Override
    public void increment(VectorValue value) {
        if (value.values.length != values.length){
            LOG.severe("Vector element-wise increment dimension mismatch");
        }
        double[] otherValues = value.values;
        for (int i = 0; i < otherValues.length; i++) {
            otherValues[i] += values[i];
        }
    }

    @Override
    public void increment(MatrixValue value) {
        LOG.severe("Incompatible dimensions of algebraic operation - matrix increment by vector");
    }

    /**
     * Default double dispatch
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
        for (int i = 0; i < values.length; i++) {
            if (maxValue.value > values[i]) {
                greater++;
            }
        }
        return greater > values.length/2;
    }

    @Override
    public boolean greaterThan(VectorValue maxValue) {
        if (maxValue.values.length != values.length){
            LOG.severe("Vector element-wise comparison dimension mismatch");
        }
        int greater = 0;
        for (int i = 0; i < values.length; i++) {
            if (maxValue.values[i] > values[i]) {
                greater++;
            }
        }
        return greater > values.length/2;
    }

    @Override
    public boolean greaterThan(MatrixValue maxValue) {
        LOG.severe("Incompatible dimensions of algebraic operation - matrix greaterThan vector");
        return false;
    }

}