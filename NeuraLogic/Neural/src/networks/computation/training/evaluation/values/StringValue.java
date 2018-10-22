package networks.computation.training.evaluation.values;

/**
 * Created by gusta on 8.3.17.
 */
public class StringValue extends Value {
    @Override
    protected Value multiplyByMatrix(MatrixValue val2) {
        return this;
    }

    @Override
    protected Value multiplyByVector(VectorValue val2) {
        return this;
    }

    @Override
    protected Value multiplyByScalar(ScalarValue val2) {
        return this;
    }
}