package networks.computation.values;

import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Value implements State.Computation {
    private static final Logger LOG = Logger.getLogger(Value.class.getName());

    public static final Value ONE = new One();
    public static final Value ZERO = new Zero();

    public abstract void initialize(ValueInitializer valueInitializer);

    public abstract void zero();

    /**
     * Double-dispatch with switch of the left-right hand sides to keep in mind!
     * todo multiplications must create new value objects!!
     *
     * @param value
     * @return
     */
    public abstract Value multiplyBy(Value value);

    public abstract Value multiplyBy(ScalarValue value);

    public abstract Value multiplyBy(VectorValue value);

    public abstract Value multiplyBy(MatrixValue value);

    /**
     * Constructive adding - will create a new Value
     *
     * @param value
     * @return
     */
    public abstract Value add(Value value);

    public abstract Value add(ScalarValue value);

    public abstract Value add(VectorValue value);

    public abstract Value add(MatrixValue value);

    /**
     * Destructive adding - faster as there is no need to create new Object Value
     * @param value
     * @return
     */
    public abstract void increment(Value value);

    public abstract void increment(ScalarValue value);

    public abstract void increment(VectorValue value);

    public abstract void increment(MatrixValue value);


    /**
     * todo consider replacing these constant classes with simple ScalarValue(1), the speedup might be small.
     */
    private static class One extends networks.computation.values.Value {

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
        public Value multiplyBy(Value value) {
            return value;
        }

        @Override
        public Value multiplyBy(ScalarValue value) {
            return value;
        }

        @Override
        public Value multiplyBy(VectorValue value) {
            return value;
        }

        @Override
        public Value multiplyBy(MatrixValue value) {
            return value;
        }

        @Override
        public Value add(Value value) {
            return value.add(one);
        }

        @Override
        public Value add(ScalarValue value) {
            return value.add(one);
        }

        @Override
        public Value add(VectorValue value) {
            return value.add(one);
        }

        @Override
        public Value add(MatrixValue value) {
            return value.add(one);
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

    private static class Zero extends networks.computation.values.Value {

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
        public Value multiplyBy(Value value) {
            return zero;
        }

        @Override
        public Value multiplyBy(ScalarValue value) {
            return zero;
        }

        @Override
        public Value multiplyBy(VectorValue value) {
            return zero;
        }

        @Override
        public Value multiplyBy(MatrixValue value) {
            return zero;
        }

        @Override
        public Value add(Value value) {
            return value;
        }

        @Override
        public Value add(ScalarValue value) {
            return value;
        }

        @Override
        public Value add(VectorValue value) {
            return value;
        }

        @Override
        public Value add(MatrixValue value) {
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