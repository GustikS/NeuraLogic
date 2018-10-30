package networks.computation.evaluation.values;

import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class ScalarValue extends Value {
    private static final Logger LOG = Logger.getLogger(ScalarValue.class.getName());
    public double value;

    public ScalarValue() {
        value = 0;
    }

    public ScalarValue(double val) {
        value = val;
    }

    public ScalarValue(ValueInitializer valueInitializer) {
        initialize(valueInitializer);
    }

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        valueInitializer.initScalar(this);
    }

    @Override
    public void zero() {
        value = 0;
    }

    @Override
    public Value multiplyBy(Value value) {
        return value.multiplyBy(this);
    }

    @Override
    public ScalarValue multiplyBy(ScalarValue value) {
        return new ScalarValue(this.value * value.value);
    }

    @Override
    public VectorValue multiplyBy(VectorValue value) {
        return null;
    }

    @Override
    public MatrixValue multiplyBy(MatrixValue value) {
        return null;
    }

    @Override
    public Value add(Value value) {
        return value.add(this);
    }

    @Override
    public ScalarValue add(ScalarValue value) {
        return new ScalarValue(this.value + value.value);
    }

    @Override
    public VectorValue add(VectorValue value) {
        return null;
    }

    @Override
    public MatrixValue add(MatrixValue value) {
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