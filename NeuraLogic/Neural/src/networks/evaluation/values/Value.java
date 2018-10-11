package networks.evaluation.values;

import networks.structure.networks.State;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Value implements State.Computation.Evaluation, State.Computation.Gradient {

    public static final Value ONE = new One();
    public static final Value ZERO = new Zero();

    public abstract void initialize(ValueInitializer valueInitializer);

    /**
     * Double-dispatch with switch of the left-right hand sides to keep in mind!
     * @param value
     * @return
     */
    public abstract Value multiplyBy(Value value);
    public abstract Value multiplyBy(ScalarValue value);
    public abstract Value multiplyBy(VectorValue value);
    public abstract Value multiplyBy(MatrixValue value);

    /**
     * Double-dispatch with switch of the left-right hand sides to keep in mind!
     * @param value
     * @return
     */
    public abstract Value add(Value value);
    public abstract Value add(ScalarValue value);
    public abstract Value add(VectorValue value);
    public abstract Value add(MatrixValue value);


    /**
     * todo consider replacing these constant classes with simple ScalarValue(1), the speedup might be small.
     */
    private static class One extends networks.evaluation.values.Value {

        private ScalarValue one = new ScalarValue(1);

        @Override
        public void initialize(ValueInitializer valueInitializer) {
            //void
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
    }

    private static class Zero extends networks.evaluation.values.Value {

        private ScalarValue zero = new ScalarValue(0);

        @Override
        public void initialize(ValueInitializer valueInitializer) {
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
    }
}