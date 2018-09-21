package networks.evaluation.values;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class VectorValue extends Value {
    double[] value;

    @Override
    protected final Value multiplyByMatrix(MatrixValue val2) {
        return this;
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

    public VectorValue(List<Double> vector) {
        value = vector.stream().mapToDouble(d -> d).toArray();
    }
}