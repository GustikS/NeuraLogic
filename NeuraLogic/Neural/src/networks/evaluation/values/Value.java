package networks.evaluation.values;

import networks.evaluation.iteration.State;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Value implements State.Computation.Evaluation, State.Computation.Gradient {

    public static final Value ONE = new One();
    public static final Value ZERO = new Zero();

    public abstract void initialize(ValueInitializer valueInitializer);

    private static class One extends networks.evaluation.values.Value {

        private ScalarValue one = new ScalarValue(1);

        @Override
        public void initialize(ValueInitializer valueInitializer) {
        }

        @Override
        protected Value multiplyByMatrix(MatrixValue val2) {
            return val2;
        }

        @Override
        protected Value multiplyByVector(VectorValue val2) {
            return val2;
        }

        @Override
        protected Value multiplyByScalar(ScalarValue val2) {
            return val2;
        }

        @Override
        protected Value addMatrix(MatrixValue val2) {
            return val2.addScalar(one);
        }

        @Override
        protected Value addVector(VectorValue val2) {
            return val2.addScalar(one);
        }

        @Override
        protected Value addScalar(ScalarValue val2) {
            return val2.addScalar(one);
        }
    }

    private static class Zero extends networks.evaluation.values.Value {

        private ScalarValue ZERO = new ScalarValue(0);

        @Override
        public void initialize(ValueInitializer valueInitializer) {
        }

        @Override
        protected Value multiplyByMatrix(MatrixValue val2) {
            return ZERO;
        }

        @Override
        protected Value multiplyByVector(VectorValue val2) {
            return ZERO;
        }

        @Override
        protected Value multiplyByScalar(ScalarValue val2) {
            return ZERO;
        }

        @Override
        protected Value addMatrix(MatrixValue val2) {
            return val2;
        }

        @Override
        protected Value addVector(VectorValue val2) {
            return val2;
        }

        @Override
        protected Value addScalar(ScalarValue val2) {
            return val2;
        }
    }

    public final Value multiplyBy(Value val2) {
        if (val2 instanceof MatrixValue) {
            return multiplyByMatrix((MatrixValue) val2);
        }
        if (val2 instanceof VectorValue) {
            return multiplyByVector((VectorValue) val2);
        }
        if (val2 instanceof ScalarValue) {
            return multiplyByScalar((ScalarValue) val2);
        }
        throw new ClassCastException("Unknown value-type multiplication!");
    }

    public final Value add(Value val2) {
        if (val2 instanceof MatrixValue) {
            return addMatrix((MatrixValue) val2);
        }
        if (val2 instanceof VectorValue) {
            return addVector((VectorValue) val2);
        }
        if (val2 instanceof ScalarValue) {
            return addScalar((ScalarValue) val2);
        }
        throw new ClassCastException("Unknown value-type adding!");
    }

    protected abstract Value multiplyByMatrix(MatrixValue val2);

    protected abstract Value multiplyByVector(VectorValue val2);

    protected abstract Value multiplyByScalar(ScalarValue val2);

    protected abstract Value addMatrix(MatrixValue val2);

    protected abstract Value addVector(VectorValue val2);

    protected abstract Value addScalar(ScalarValue val2);

}