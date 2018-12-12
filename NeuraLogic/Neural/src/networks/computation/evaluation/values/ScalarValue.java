package networks.computation.evaluation.values;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;
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


    @NotNull
    @Override
    public Iterator<Double> iterator() {
        return new valueIterator();
    }

    protected class valueIterator implements Iterator<Double> {
        int i = 0;

        @Override
        public boolean hasNext() {
            return i++ == 0;
        }

        @Override
        public Double next() {
            return value;
        }
    }

    @Override
    public void initialize(ValueInitializer valueInitializer) {
        valueInitializer.initScalar(this);
    }

    @Override
    public ScalarValue zero() {
        value = 0;
        return this;
    }

    @Override
    public Value clone() {
        return new ScalarValue(value);
    }

    @Override
    public Value getForm() {
        return new ScalarValue(0);
    }

    @Override
    public int[] size() {
        return new int[0];
    }

    @Override
    public Value apply(Function<Double, Double> function) {
        return new ScalarValue(function.apply(value));
    }

    /**
     * Default Double Dispatch
     *
     * @param value
     * @return
     */
    @Override
    public Value times(Value value) {
        return value.times(this);
    }

    @Override
    public ScalarValue times(ScalarValue value) {
        return new ScalarValue(this.value * value.value);
    }

    @Override
    public VectorValue times(VectorValue vector) {
        VectorValue clone = vector.clone();
        for (int i = 0; i < vector.value.length; i++) {
            clone.value[i] *= this.value;
        }
        return clone;
    }

    @Override
    public MatrixValue times(MatrixValue value) {
        MatrixValue clone = value.clone();
        for (int i = 0; i < clone.rows; i++) {
            for (int j = 0; j < clone.cols; j++) {
                clone.value[i][j] *= this.value;
            }
        }
        return clone;
    }

    /**
     * Default Double Dispatch
     *
     * @param value
     * @return
     */
    @Override
    public Value plus(Value value) {
        return value.plus(this);
    }

    @Override
    public ScalarValue plus(ScalarValue value) {
        return new ScalarValue(this.value + value.value);
    }

    @Override
    public VectorValue plus(VectorValue value) {
        VectorValue clone = value.clone();
        for (int i = 0; i < clone.value.length; i++) {
            clone.value[i] += this.value;
        }
        return clone;
    }

    @Override
    public MatrixValue plus(MatrixValue value) {
        MatrixValue clone = value.clone();
        for (int i = 0; i < clone.rows; i++) {
            for (int j = 0; j < clone.cols; j++) {
                clone.value[i][j] += this.value;
            }
        }
        return clone;
    }

    /**
     * Default Double Dispatch
     *
     * @param value
     * @return
     */
    @Override
    public Value minus(Value value) {
        return value.plus(this);
    }

    /**
     * DD - switch of sides??!! todo check
     *
     * @param value
     * @return
     */
    @Override
    public ScalarValue minus(ScalarValue value) {
        return new ScalarValue(value.value - this.value);
    }

    /**
     * DD - switch of sides??!! todo check
     *
     * @param value
     * @return
     */
    @Override
    public VectorValue minus(VectorValue value) {
        VectorValue clone = value.clone();
        for (int i = 0; i < clone.value.length; i++) {
            clone.value[i] -= this.value;
        }
        return clone;
    }

    /**
     * DD - switch of sides??!! todo check
     *
     * @param value
     * @return
     */
    @Override
    public MatrixValue minus(MatrixValue value) {
        MatrixValue clone = value.clone();
        for (int i = 0; i < clone.rows; i++) {
            for (int j = 0; j < clone.cols; j++) {
                clone.value[i][j] -= this.value;
            }
        }
        return clone;
    }

    /**
     * Default Double Dispatch
     *
     * @param value
     */
    @Override
    public void increment(Value value) {
        value.increment(this);
    }

    @Override
    public void increment(ScalarValue value) {
        value.value += this.value;
    }

    @Override
    public void increment(VectorValue value) {
        for (int i = 0; i < value.value.length; i++) {
            value.value[i] += this.value;
        }
    }

    @Override
    public void increment(MatrixValue value) {
        for (int i = 0; i < value.rows; i++) {
            for (int j = 0; j < value.cols; j++) {
                value.value[i][j] += this.value;
            }
        }
    }

    @Override
    public boolean greaterThan(Value maxValue) {
        return maxValue.greaterThan(this);
    }

    @Override
    public boolean greaterThan(ScalarValue maxValue) {
        return maxValue.value > this.value;
    }

    @Override
    public boolean greaterThan(VectorValue maxValue) {
        int greater = 0;
        for (int i = 0; i < maxValue.value.length; i++) {
            if (maxValue.value[i] > this.value) {
                greater++;
            }
        }
        return greater > maxValue.value.length/2;
    }

    @Override
    public boolean greaterThan(MatrixValue maxValue) {
        int greater = 0;
        for (int i = 0; i < maxValue.rows; i++) {
            for (int j = 0; j < maxValue.cols; j++) {
                if (maxValue.value[i][j] > this.value) {
                    greater++;
                }
            }
        }
        return greater > maxValue.cols * maxValue.rows / 2;
    }

}