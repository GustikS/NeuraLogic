package networks.computation.evaluation.values;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class VectorValue extends Value {
    private static final Logger LOG = Logger.getLogger(VectorValue.class.getName());
    double[] value;

    public VectorValue(int size) {
        value = new double[size];
    }

    public VectorValue(List<Double> vector) {
        value = vector.stream().mapToDouble(d -> d).toArray();
    }

    public VectorValue(int size, ValueInitializer valueInitializer) {
        value = new double[size];
        initialize(valueInitializer);
    }


    @NotNull
    @Override
    public Iterator<Double> iterator() {
        return new VectorValue.valueIterator();
    }

    protected class valueIterator implements Iterator<Double> {
        int i = 0;

        @Override
        public boolean hasNext() {
            return i++ < value.length;
        }

        @Override
        public Double next() {
            return value[i];
        }
    }

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        valueInitializer.initVector(this);
    }

    @Override
    public VectorValue zero() {
        for (int i = 0; i < value.length; i++) {
            value[i] = 0;
        }
        return this;
    }

    @Override
    public VectorValue clone() {
        VectorValue clone = new VectorValue(value.length);
        for (int i = 0; i < clone.value.length; i++) {
            clone.value[i] = this.value[i];
        }
        return clone;
    }

    @Override
    public Value getForm() {
        return new VectorValue(value.length);
    }

    @Override
    public int[] size() {
        return new int[0];
    }

    @Override
    public Value apply(Function<Double, Double> function) {
        return null;
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
    public Value minus(Value value) {
        return null;
    }

    @Override
    public Value minus(ScalarValue value) {
        LOG.severe("Invalid dimensions for algebraic operation");

        return null;
    }

    @Override
    public Value minus(VectorValue value) {
        return null;
    }

    @Override
    public Value minus(MatrixValue value) {
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
    public boolean greaterThan(Value maxValue) {
        return false;
    }

    @Override
    public boolean greaterThan(ScalarValue maxValue) {
        return false;
    }

    @Override
    public boolean greaterThan(VectorValue maxValue) {
        return false;
    }

    @Override
    public boolean greaterThan(MatrixValue maxValue) {
        return false;
    }

}