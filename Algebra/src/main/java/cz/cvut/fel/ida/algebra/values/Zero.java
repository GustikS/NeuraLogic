package cz.cvut.fel.ida.algebra.values;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.function.DoubleUnaryOperator;
import java.util.logging.Logger;

/**
 * A constant abstract 0 to make some operations faster
 */
class Zero extends Value {

    private static final Logger LOG = Logger.getLogger(Zero.class.getName());

    private final ScalarValue zero = new ScalarValue(0);

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        //void
    }

    @Override
    public Value zero() {
        return zero;
    }

    @Override
    public Value clone() {
        LOG.fine("Cloning a constant ZERO");
        return this;
    }

    @Override
    public Value getForm() {
        LOG.fine("Getting Form of a constant ZERO");
        return this;
    }

    @Override
    public void transpose() {
    }

    @Override
    public Value transposedView() {
        return this;
    }

    @Override
    public int[] size() {
        return new int[0];
    }

    @Override
    public Value slice(int[] rows, int[] cols) {
        return this.zero.slice(rows, cols);
    }

    @Override
    public Value reshape(int[] shape) {
        return this.zero.reshape(shape).clone();
    }

    @Override
    public double[] getAsArray() {
        return new double[]{zero.value};
    }

    @Override
    public void setAsArray(double[] value) {
        LOG.warning("Trying to set value of constant ZERO");
    }

    @Override
    public Value apply(DoubleUnaryOperator function) {
        throw new ArithmeticException("Trying to modify value of constant ZERO");
    }

    @Override
    public void applyInplace(DoubleUnaryOperator function) {
        throw new ArithmeticException("Trying to modify value of constant ZERO");
    }

    @Override
    public double get(int i) {
        if (i != 0) {
            LOG.severe("Scalar value: asking for i-th element!");
        }
        return zero.value;
    }

    @Override
    public void set(int i, double value) {
        if (i != 0) {
            LOG.severe("Scalar value: asking for i-th element!");
        }
        LOG.warning("Trying to set value i constant ONE");
    }

    @Override
    public void increment(int i, double value) {
        if (i != 0) {
            LOG.severe("Scalar value: asking for i-th element!");
        }
        LOG.warning("Trying to increment value i constant ONE");
    }

    @Override
    public String toString(NumberFormat numberFormat) {
        return "0";
    }
    
    @Override
    public Value times(Value value) {
        return value.getForm();
    }

    @Override
    public Value times(ScalarValue value) {
        return value.getForm();
    }

    @Override
    public Value times(VectorValue value) {
        return value.getForm();
    }

    @Override
    public Value times(MatrixValue value) {
        return value.getForm();
    }

    @Override
    protected Value times(TensorValue value) {
        return value.getForm();
    }

    @Override
    public Value elementTimes(Value value) {
        return value.getForm();
    }

    @Override
    protected Value elementTimes(ScalarValue value) {
        return value.getForm();
    }

    @Override
    protected Value elementTimes(VectorValue value) {
        return value.getForm();
    }

    @Override
    protected Value elementTimes(MatrixValue value) {
        return value.getForm();
    }

    @Override
    protected Value elementTimes(TensorValue value) {
        return value.getForm();
    }

    @Override
    public Value transposedTimes(Value value) {
        return value.getForm();
    }

    @Override
    protected Value transposedTimes(ScalarValue value) {
        return value.getForm();
    }

    @Override
    protected Value transposedTimes(VectorValue value) {
        return value.getForm().transposedView();
    }

    @Override
    protected Value transposedTimes(MatrixValue value) {
        return value.getForm().transposedView();
    }

    @Override
    protected Value transposedTimes(TensorValue value) {
        return value.getForm().transposedView();
    }

    @Override
    public Value kroneckerTimes(Value value) {
        return value.getForm();
    }

    @Override
    protected Value kroneckerTimes(ScalarValue value) {
        return value.getForm();
    }

    @Override
    protected Value kroneckerTimes(VectorValue value) {
        return value.getForm();
    }

    @Override
    protected Value kroneckerTimes(MatrixValue value) {
        return value.getForm();
    }

    @Override
    protected Value kroneckerTimes(TensorValue value) {
        return value.getForm();
    }

    @Override
    public Value elementDivideBy(Value value) {
        return zero;
    }

    @Override
    protected Value elementDivideBy(ScalarValue value) {
        throw new ArithmeticException("Division by zero");
    }

    @Override
    protected Value elementDivideBy(VectorValue value) {
        throw new ArithmeticException("Division by zero");
    }

    @Override
    protected Value elementDivideBy(MatrixValue value) {
        throw new ArithmeticException("Division by zero");
    }

    @Override
    protected Value elementDivideBy(TensorValue value) {
        throw new ArithmeticException("Division by zero");
    }

    @Override
    public Value plus(Value value) {
        return value;
    }

    @Override
    public Value plus(ScalarValue value) {
        return value;
    }

    @Override
    public Value plus(VectorValue value) {
        return value;
    }

    @Override
    public Value plus(MatrixValue value) {
        return value;
    }

    @Override
    protected Value plus(TensorValue value) {
        return value;
    }

    @Override
    public Value minus(Value value) {
        return value;
    }

    @Override
    public Value minus(ScalarValue value) {
        return value;
    }

    @Override
    public Value minus(VectorValue value) {
        return value;
    }

    @Override
    public Value minus(MatrixValue value) {
        return value;
    }

    @Override
    protected Value minus(TensorValue value) {
        return value;
    }

    @Override
    public void incrementBy(Value value) {
        LOG.warning("Trying to increment a constant ZERO");
    }

    @Override
    public void incrementBy(ScalarValue value) {
        //increment by zero = no increment
    }

    @Override
    public void incrementBy(VectorValue value) {
        //increment by zero = no increment
    }

    @Override
    public void incrementBy(MatrixValue value) {
        //increment by zero = no increment
    }

    @Override
    protected void incrementBy(TensorValue value) {
        //increment by zero = no increment
    }

    @Override
    public void elementMultiplyBy(Value value) {
        LOG.warning("Trying to increment a constant ZERO");
    }

    @Override
    protected void elementMultiplyBy(ScalarValue value) {
        value.zero();
    }

    @Override
    protected void elementMultiplyBy(VectorValue value) {
        value.zero();
    }

    @Override
    protected void elementMultiplyBy(MatrixValue value) {
        value.zero();
    }

    @Override
    protected void elementMultiplyBy(TensorValue value) {
        value.zero();
    }

    @Override
    public boolean greaterThan(Value maxValue) {
        return maxValue.greaterThan(zero);
    }

    @Override
    public boolean greaterThan(ScalarValue maxValue) {
        return zero.greaterThan(maxValue);
    }

    @Override
    public boolean greaterThan(VectorValue maxValue) {
        return zero.greaterThan(maxValue);
    }

    @Override
    public boolean greaterThan(MatrixValue maxValue) {
        return zero.greaterThan(maxValue);
    }

    @Override
    protected boolean greaterThan(TensorValue maxValue) {
        return zero.greaterThan(maxValue);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Value obj) {
        if (obj instanceof Zero) {
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<Double> iterator() {
        return zero.iterator();
    }
}
