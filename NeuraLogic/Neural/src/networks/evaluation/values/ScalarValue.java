package networks.evaluation.values;

/**
 * Created by gusta on 8.3.17.
 */
public class ScalarValue extends Value {
    double value;

    @Override
    protected Value multiplyByScalar(ScalarValue val2) {
        return null;
    }

    @Override
    protected Value multiplyByMatrix(MatrixValue val2) {
        return null;
    }

    @Override
    protected Value multiplyByVector(VectorValue val2) {
        return null;
    }

    public ScalarValue(double val) {
        value = val;
    }
}