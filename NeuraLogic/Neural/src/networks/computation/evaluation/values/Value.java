package networks.computation.evaluation.values;

import com.sun.istack.internal.NotNull;
import networks.computation.evaluation.values.distributions.ValueInitializer;

import java.util.Iterator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Custom algebraic objects with standard semantics, i.e. scalar, vector and matrix with corresponding operations.
 * By default all the operations are considered as element-wise wherever possible.
 *
 * @implements Iterable which is meant to return the underlying elementary values in a predefined order, i.e. a serialization.
 *
 * Created by gusta on 8.3.17.
 */
public abstract class Value implements Iterable<Double> {
    private static final Logger LOG = Logger.getLogger(Value.class.getName());

    /**
     * Random elementary values
     * @param valueInitializer
     */
    public abstract void initialize(ValueInitializer valueInitializer);

    /**
     * Zero elementary values
     * @return
     */
    public abstract Value zero();

    /**
     * Deep copy
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
     * Dimension representation up to a 2D matrix
     * @return
     */
    public abstract int[] size();

    /**
     * Element-wise application of a given real function
     * @param function
     * @return
     */
    public abstract Value apply(Function<Double, Double> function);

    /**
     * CONSTRUCTIVE multiplication, i.e. creation of a NEW Value under the hood to be returned.
     *
     * Uses double-dispatch with switch of the left-right hand sides to keep in mind!
     *
     * @param value
     * @return
     */
    public abstract Value times(Value value);

    public abstract Value times(ScalarValue value);

    public abstract Value times(VectorValue value);

    public abstract Value times(MatrixValue value);

    /**
     * CONSTRUCTIVE adding - will create a new Value.
     *
     * @param value
     * @return
     */
    public abstract Value plus(Value value);

    public abstract Value plus(ScalarValue value);

    public abstract Value plus(VectorValue value);

    public abstract Value plus(MatrixValue value);

    /**
     * CONSTRUCTIVE subtracting - will create a new Value.
     *
     * @param value
     * @return
     */
    public abstract Value minus(Value value);   //todo this must be element-wise!

    public abstract Value minus(ScalarValue value);

    public abstract Value minus(VectorValue value);

    public abstract Value minus(MatrixValue value);

    /**
     * DESTRUCTIVE adding - changes the original Value.
     * - faster as there is no need to create new Value Object.
     *
     * @param value
     * @return
     */
    public abstract void increment(Value value);

    public abstract void increment(ScalarValue value);

    public abstract void increment(VectorValue value);

    public abstract void increment(MatrixValue value);


    /**
     * Comparison with ad-hoc semantics (based on majority) for Values of different dimensions.
     * @param maxValue
     * @return
     */
    public abstract boolean greaterThan(Value maxValue);

    public abstract boolean greaterThan(ScalarValue maxValue);

    public abstract boolean greaterThan(VectorValue maxValue);

    public abstract boolean greaterThan(MatrixValue maxValue);


    //----------todo consider replacing these constant classes with simple ScalarValue(0/1), the speedup might be small.

    public static final Value ZERO = new Zero();
    public static final Value ONE = new One();

    @Deprecated
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
            return null;
        }

        @Override
        public Value getForm() {
            return null;
        }

        @Override
        public int[] size() {
            return new int[0];
        }

        @Override
        public Value apply(Function<Double, Double> function) {
            return null;
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
            return null;
        }

        @Override
        public Value minus(ScalarValue value) {
            return null;
        }

        @Override
        public Value minus(VectorValue value) {
            return null;
        }

        @Override
        public Value minus(MatrixValue value) {
            return null;
        }

        @Override
        public void increment(Value value) {

        }

        @Override
        public void increment(ScalarValue value) {

        }

        @Override
        public void increment(VectorValue value) {

        }

        @Override
        public void increment(MatrixValue value) {

        }

        @Override
        public boolean greaterThan(Value maxValue) {
            return false;
        }

        @Override
        public boolean greaterThan(ScalarValue maxValue) {
            return false;
        }

        @Override
        public boolean greaterThan(VectorValue maxValue) {
            return false;
        }

        @Override
        public boolean greaterThan(MatrixValue maxValue) {
            return false;
        }

        @NotNull
        @Override
        public Iterator<Double> iterator() {
            return null;
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
            return null;
        }

        @Override
        public Value getForm() {
            return null;
        }

        @Override
        public int[] size() {
            return new int[0];
        }

        @Override
        public Value apply(Function<Double, Double> function) {
            return null;
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
            return null;
        }

        @Override
        public Value minus(ScalarValue value) {
            return null;
        }

        @Override
        public Value minus(VectorValue value) {
            return null;
        }

        @Override
        public Value minus(MatrixValue value) {
            return null;
        }

        @Override
        public void increment(Value value) {

        }

        @Override
        public void increment(ScalarValue value) {

        }

        @Override
        public void increment(VectorValue value) {

        }

        @Override
        public void increment(MatrixValue value) {

        }

        @Override
        public boolean greaterThan(Value maxValue) {
            return false;
        }

        @Override
        public boolean greaterThan(ScalarValue maxValue) {
            return false;
        }

        @Override
        public boolean greaterThan(VectorValue maxValue) {
            return false;
        }

        @Override
        public boolean greaterThan(MatrixValue maxValue) {
            return false;
        }

        @NotNull
        @Override
        public Iterator<Double> iterator() {
            return null;
        }
    }
}