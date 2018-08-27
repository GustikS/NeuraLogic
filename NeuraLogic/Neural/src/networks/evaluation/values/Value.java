package networks.evaluation.values;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Value {

    public Value multiplyBy(Value val2) {
        if (val2 instanceof MatrixValue) {
            return multiplyByMatrix((MatrixValue)val2);
        }
        if (val2 instanceof VectorValue) {
            return multiplyByVector((VectorValue)val2);
        }
        if (val2 instanceof ScalarValue) {
            return multiplyByScalar((ScalarValue)val2);
        }
        throw new ClassCastException("Unknown value-type multiplication!");
    }

    public Value add(Value val2) {
        if (val2 instanceof MatrixValue) {
            return addMatrix((MatrixValue)val2);
        }
        if (val2 instanceof VectorValue) {
            return addVector((VectorValue)val2);
        }
        if (val2 instanceof ScalarValue) {
            return addScalar((ScalarValue)val2);
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