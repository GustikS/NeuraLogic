package networks.computation.evaluation.values;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class VectorValue extends Value {
    private static final Logger LOG = Logger.getLogger(VectorValue.class.getName());
    double[] value;

    public VectorValue(int size){
        value = new double[size];
    }

    public VectorValue(List<Double> vector) {
        value = vector.stream().mapToDouble(d -> d).toArray();
    }

    public VectorValue(int size, ValueInitializer valueInitializer){
        value = new double[size];
        initialize(valueInitializer);
    }

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        valueInitializer.initVector(this);
    }

    @Override
    public void zero() {
        for (int i = 0; i < value.length; i++) {
            value[i] = 0;
        }
    }

    @Override
    public Value times(Value value) {
        return value.times(this);
    }

    @Override
    public VectorValue times(ScalarValue value) {
        return null;
    }

    @Override
    public Value times(VectorValue value) {
        //todo take care of an element-wise multiplication vs matrix
        return null;
    }

    @Override
    public MatrixValue times(MatrixValue value) {
        return null;
    }

    @Override
    public Value plus(Value value) {
        return null;
    }

    @Override
    public VectorValue plus(ScalarValue value) {
        return null;
    }

    @Override
    public VectorValue plus(VectorValue value) {
        return null;
    }

    @Override
    public Value plus(MatrixValue value) {
        LOG.severe("Incompatible multiplication");
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