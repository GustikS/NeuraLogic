package networks.computation.training.evaluation.values;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class MatrixValue extends Value {
    private static final Logger LOG = Logger.getLogger(MatrixValue.class.getName());

    int rows;
    int cols;

    double[][] value;

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        valueInitializer.initMatrix(this);
    }

    @Override
    public void zero() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                value[i][j] = 0;
            }
        }
    }

    @Override
    public Value multiplyBy(Value value) {
        return value.multiplyBy(this);
    }

    @Override
    public ScalarValue multiplyBy(ScalarValue value) {
        return null;
    }

    @Override
    public MatrixValue multiplyBy(VectorValue value) {
        return null;
    }

    /**
     * Handle the non-commutative cases carefully!
     * @param value
     * @return
     */
    @Override
    public MatrixValue multiplyBy(MatrixValue value) {
        MatrixValue lhs = value;
        MatrixValue rhs = this;
        //todo actually multiply them (with eigen?)
        return null;
    }

    @Override
    public Value add(Value value) {
        return value.add(this);
    }

    @Override
    public MatrixValue add(ScalarValue value) {
        //element-wise
        return null;
    }

    @Override
    public Value add(VectorValue value) {
        LOG.severe("Incompatible multiplication");
        return null;
    }

    @Override
    public Value add(MatrixValue value) {
        //check dimensions
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
    public void invalidate() {

    }
}
