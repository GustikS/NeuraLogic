package networks.evaluation.values;

/**
 * Created by gusta on 8.3.17.
 */
public class MatrixValue extends Value {
    double[][] value;

    @Override
    protected final Value multiplyByMatrix(MatrixValue val2) {
        return null;
    }

    @Override
    protected final Value multiplyByVector(VectorValue val2) {
        return null;
    }

    @Override
    protected final Value multiplyByScalar(ScalarValue val2) {
        return null;
    }

    @Override
    protected final Value addMatrix(MatrixValue val2) {
        return null;
    }

    @Override
    protected final Value addVector(VectorValue val2) {
        return null;
    }

    @Override
    protected final Value addScalar(ScalarValue val2) {
        return null;
    }
}
