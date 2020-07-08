package cz.cvut.fel.ida.algebra.values;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;

import java.util.Iterator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A constant abstract 1 to make some operations faster
 */
class One extends Value {

    private static final Logger LOG = Logger.getLogger(One.class.getName());

    private ScalarValue one = new ScalarValue(1);

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        //void
    }

    @Override
    public Value zero() {
        LOG.warning("Constant One cannot be zeroed!");
        return null;
    }

    @Override
    public Value clone() {
        return new ScalarValue(1);
    }

    @Override
    public Value getForm() {
        return new ScalarValue(0);
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
    public Value apply(Function<Double, Double> function) {
        throw new ArithmeticException("Trying to modify value of constant ONE");
    }

    @Override
    public double get(int i) {
        if (i != 0) {
            LOG.severe("Scalar value: asking for i-th element!");
        }
        return one.value;
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
        LOG.warning("Trying to increment value i constant ONE");
    }

    @Override
    public String toString() {
        return "1";
    }

    @Override
    public Value times(Value value) {
        return value;
    }

    @Override
    public Value times(ScalarValue value) {
        return value;
    }

    @Override
    public Value times(VectorValue value) {
        return value;
    }

    @Override
    public Value times(MatrixValue value) {
        return value;
    }

    @Override
    protected Value times(TensorValue value) {
        return value;
    }

    @Override
    public Value elementTimes(Value value) {
        return value;
    }

    @Override
    protected Value elementTimes(ScalarValue value) {
        return value;
    }

    @Override
    protected Value elementTimes(VectorValue value) {
        return value;
    }

    @Override
    protected Value elementTimes(MatrixValue value) {
        return value;
    }

    @Override
    protected Value elementTimes(TensorValue value) {
        return value;
    }

    @Override
    public Value kroneckerTimes(Value value) {
        return value;
    }

    @Override
    protected Value kroneckerTimes(ScalarValue value) {
        return value;
    }

    @Override
    protected Value kroneckerTimes(VectorValue value) {
        return value;
    }

    @Override
    protected Value kroneckerTimes(MatrixValue value) {
        return value;
    }

    @Override
    protected Value kroneckerTimes(TensorValue value) {
        return value;
    }

    @Override
    public Value elementDivideBy(Value value) {
        return value;
    }

    @Override
    protected Value elementDivideBy(ScalarValue value) {
        return value;
    }

    @Override
    protected Value elementDivideBy(VectorValue value) {
        return value;
    }

    @Override
    protected Value elementDivideBy(MatrixValue value) {
        return value;
    }

    @Override
    protected Value elementDivideBy(TensorValue value) {
        return value;
    }

    @Override
    public Value plus(Value value) {
        return value.plus(one);
    }

    @Override
    public Value plus(ScalarValue value) {
        return value.plus(one);
    }

    @Override
    public Value plus(VectorValue value) {
        return value.plus(one);
    }

    @Override
    public Value plus(MatrixValue value) {
        return value.plus(one);
    }

    @Override
    protected Value plus(TensorValue value) {
        return value.plus(one);
    }

    @Override
    public Value minus(Value value) {
        return value.minus(one);
    }

    @Override
    public Value minus(ScalarValue value) {
        return one.minus(value);
    }

    @Override
    public Value minus(VectorValue value) {
        return one.minus(value);
    }

    @Override
    public Value minus(MatrixValue value) {
        return one.minus(value);
    }

    @Override
    protected Value minus(TensorValue value) {
        return one.minus(value);
    }

    @Override
    public void incrementBy(Value value) {
        LOG.warning("Trying to increment a constant ONE");
    }

    @Override
    public void incrementBy(ScalarValue value) {
        one.incrementBy(value);
    }

    @Override
    public void incrementBy(VectorValue value) {
        one.incrementBy(value);
    }

    @Override
    public void incrementBy(MatrixValue value) {
        one.incrementBy(value);
    }

    @Override
    protected void incrementBy(TensorValue value) {
        one.incrementBy(value);
    }

    @Override
    public void elementMultiplyBy(Value value) {
        LOG.warning("Trying to multiplyBy a constant ONE");
    }

    @Override
    protected void elementMultiplyBy(ScalarValue value) {
//        one.multiplyBy(value);
    }

    @Override
    protected void elementMultiplyBy(VectorValue value) {
//        one.multiplyBy(value);
    }

    @Override
    protected void elementMultiplyBy(MatrixValue value) {
//        one.multiplyBy(value);
    }

    @Override
    protected void elementMultiplyBy(TensorValue value) {
//        one.multiplyBy(value);
    }

    @Override
    public boolean greaterThan(Value maxValue) {
        return maxValue.greaterThan(one);
    }

    @Override
    public boolean greaterThan(ScalarValue maxValue) {
        return one.greaterThan(maxValue);
    }

    @Override
    public boolean greaterThan(VectorValue maxValue) {
        return one.greaterThan(maxValue);
    }

    @Override
    public boolean greaterThan(MatrixValue maxValue) {
        return one.greaterThan(maxValue);
    }

    @Override
    protected boolean greaterThan(TensorValue maxValue) {
        return one.greaterThan(maxValue);
    }

    @Override
    public boolean equals(Value obj) {
        if (obj instanceof One) {
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<Double> iterator() {
        return one.iterator();
    }
}
