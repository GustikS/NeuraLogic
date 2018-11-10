package networks.computation.evaluation.values;

import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Value implements State.Neural {
    private static final Logger LOG = Logger.getLogger(Value.class.getName());

    public static final Value ONE = new One();
    public static final Value ZERO = new Zero();

    public abstract void initialize(ValueInitializer valueInitializer);

    public abstract void zero();

    public abstract Value clone();

    /**
     * Double-dispatch with switch of the left-right hand sides to keep in mind!
     * todo multiplications must create new value objects!!
     *
     * @param value
     * @return
     */
    public abstract Value times(Value value);

    public abstract Value times(ScalarValue value);

    public abstract Value times(VectorValue value);

    public abstract Value times(MatrixValue value);

    /**
     * Constructive adding - will create a new Value
     *
     * @param value
     * @return
     */
    public abstract Value plus(Value value);

    public abstract Value plus(ScalarValue value);

    public abstract Value plus(VectorValue value);

    public abstract Value plus(MatrixValue value);

    /**
     * Constructive subtracting - will create a new Value
     * @param value
     * @return
     */
    public abstract Value minus(Value value);   //todo this must be element-wise!

    public abstract Value minus(ScalarValue value);

    public abstract Value minus(VectorValue value);

    public abstract Value minus(MatrixValue value);

    /**
     * Destructive adding - faster as there is no need to create new Object Value
     * @param value
     * @return
     */
    public abstract void increment(Value value);

    public abstract void increment(ScalarValue value);

    public abstract void increment(VectorValue value);

    public abstract void increment(MatrixValue value);

    public abstract boolean greaterThan(Value maxValue);
    public abstract boolean greaterThan(ScalarValue maxValue);
    public abstract boolean greaterThan(VectorValue maxValue);
    public abstract boolean greaterThan(MatrixValue maxValue);


    /**
     * todo consider replacing these constant classes with simple ScalarValue(1), the speedup might be small.
     */
    private static class One extends Value {

        private ScalarValue one = new ScalarValue(1);

        @Override
        public void initialize(ValueInitializer valueInitializer) {
            //void
        }

        @Override
        public void zero() {
            LOG.warning("Constant One cannot be zeroed!");
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
        public void invalidate() {

        }
    }

    private static class Zero extends Value {

        private ScalarValue zero = new ScalarValue(0);

        @Override
        public void initialize(ValueInitializer valueInitializer) {
            //void
        }

        @Override
        public void zero() {
            //void
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
        public void invalidate() {

        }
    }
}