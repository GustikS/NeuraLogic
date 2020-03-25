package cz.cvut.fel.ida.algebra.values;

import com.sun.istack.internal.NotNull;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Custom algebraic objects with standard semantics, i.e. scalar, vector and matrix with corresponding operations.
 * By default all the operations are considered as element-wise wherever possible.
 *
 * @implements Iterable which is meant to return the underlying elementary values in a predefined order, i.e. a serialization.
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class Value implements Iterable<Double>, Serializable {   //todo add division
    private static final Logger LOG = Logger.getLogger(Value.class.getName());

    /**
     * Random elementary values
     *
     * @param valueInitializer
     */
    public abstract void initialize(ValueInitializer valueInitializer);

    /**
     * Zero elementary values
     *
     * @return
     */
    public abstract Value zero();

    /**
     * Deep copy
     *
     * @return
     */
    public abstract Value clone();

    /**
     * Equivalent to clone() + zero(), but faster
     *
     * @return
     */
    public abstract Value getForm();

    /**
     * Transpose this Value
     *
     * @return
     */
    public abstract void transpose();

    /**
     * Returns a transposed view on the same data (except for matrix values, which throws an error instead)
     *
     * @return
     */
    public abstract Value transposedView();

    /**
     * Dimension representation up to a 2D matrix
     *
     * @return
     */
    public abstract int[] size();

    /**
     * Element-wise application of a given real function
     *
     * @param function
     * @return
     */
    public abstract Value apply(Function<Double, Double> function);

    /**
     * Since Value is Iterable<Double>, we can access i-th element
     *
     * @param i
     * @return
     */
    public abstract double get(int i);

    /**
     * Set i-th element
     *
     * @param i
     * @param value
     */
    public abstract void set(int i, double value);

    /**
     * A faster shortcut to get+set for incrementation of particular element
     *
     * @param i
     * @param value
     */
    public abstract void increment(int i, double value);

    @Override
    public abstract String toString();

    /**
     * Can be used for debugging
     *
     * @return
     */
    public String toDetailedString() {
        return toString();
    }

    /**
     * CONSTRUCTIVE multiplication, i.e. creation of a NEW Value under the hood to be returned.
     * <p>
     * Uses double-dispatch with switch of the left-right hand sides to keep in mind!
     *
     * @param value
     * @return
     */
    public abstract Value times(Value value);

    protected abstract Value times(ScalarValue value);  //always keep the specific dispatch methods protected from outside calls so that it always goes correctly through the double dispatch as expected

    protected abstract Value times(VectorValue value);

    protected abstract Value times(MatrixValue value);

    /**
     * ELEMENT-wise product
     *
     * @param value
     * @return
     */
    public abstract Value elementTimes(Value value);

    protected abstract Value elementTimes(ScalarValue value);

    protected abstract Value elementTimes(VectorValue value);

    protected abstract Value elementTimes(MatrixValue value);

    /**
     * CONSTRUCTIVE adding - will create a new Value.
     *
     * @param value
     * @return
     */
    public abstract Value plus(Value value);

    protected abstract Value plus(ScalarValue value);

    protected abstract Value plus(VectorValue value);

    protected abstract Value plus(MatrixValue value);

    /**
     * CONSTRUCTIVE subtracting - will create a new Value.
     *
     * @param value
     * @return
     */
    public abstract Value minus(Value value);   //todo this must be element-wise!

    protected abstract Value minus(ScalarValue value);

    protected abstract Value minus(VectorValue value);

    protected abstract Value minus(MatrixValue value);

    /**
     * DESTRUCTIVE adding - changes the INPUT Value.
     * - faster as there is no need to create new Value Object.
     *
     * @param value -THIS ONE WILL GET INCREMENTED BY THE CALLER!!
     * @return
     */
    public abstract void incrementBy(Value value);

    protected abstract void incrementBy(ScalarValue value);

    protected abstract void incrementBy(VectorValue value);

    protected abstract void incrementBy(MatrixValue value);


    /**
     * Comparison with ad-hoc semantics (based on majority) for Values of different dimensions.
     *
     * @param maxValue
     * @return
     */
    public abstract boolean greaterThan(Value maxValue);    //todo add GTE version and Equals version, too

    protected abstract boolean greaterThan(ScalarValue maxValue);

    protected abstract boolean greaterThan(VectorValue maxValue);

    protected abstract boolean greaterThan(MatrixValue maxValue);


    //----------todo consider replacing these constant classes with simple ScalarValue(0/1), the speedup might be small.

    public static final Value ZERO = new Zero();
    public static final Value ONE = new One();

    private static class One extends Value {

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
            return one.apply(function);
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

        @NotNull
        @Override
        public Iterator<Double> iterator() {
            return one.iterator();
        }
    }

    @Deprecated
    private static class Zero extends Value {

        private ScalarValue zero = new ScalarValue(0);

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
            LOG.warning("Cloning a constant ZERO");
            return this;
        }

        @Override
        public Value getForm() {
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
        public Value apply(Function<Double, Double> function) {
            return zero.apply(function);
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
        public String toString() {
            return "0";
        }

        @Override
        public Value times(Value value) {
            return zero;
        }

        @Override
        public Value times(ScalarValue value) {
            return zero;
        }

        @Override
        public Value times(VectorValue value) {
            return zero;
        }

        @Override
        public Value times(MatrixValue value) {
            return zero;
        }

        @Override
        public Value elementTimes(Value value) {
            return zero;
        }

        @Override
        protected Value elementTimes(ScalarValue value) {
            return zero;
        }

        @Override
        protected Value elementTimes(VectorValue value) {
            return zero;
        }

        @Override
        protected Value elementTimes(MatrixValue value) {
            return zero;
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

        @NotNull
        @Override
        public Iterator<Double> iterator() {
            return zero.iterator();
        }
    }
}