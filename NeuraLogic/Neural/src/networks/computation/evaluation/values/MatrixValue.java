package networks.computation.evaluation.values;

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
    public Value times(Value value) {
        return value.times(this);
    }

    @Override
    public ScalarValue times(ScalarValue value) {
        return null;
    }

    @Override
    public MatrixValue times(VectorValue value) {
        return null;
    }

    /**
     * Handle the non-commutative cases carefully!
     * @param value
     * @return
     */
    @Override
    public MatrixValue times(MatrixValue value) {
        MatrixValue lhs = value;
        MatrixValue rhs = this;
        //todo actually multiply them (with eigen?)
        return null;
    }

    @Override
    public Value plus(Value value) {
        return value.plus(this);
    }

    @Override
    public MatrixValue plus(ScalarValue value) {
        //element-wise
        return null;
    }

    @Override
    public Value plus(VectorValue value) {
        LOG.severe("Incompatible multiplication");
        return null;
    }

    @Override
    public Value plus(MatrixValue value) {
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
