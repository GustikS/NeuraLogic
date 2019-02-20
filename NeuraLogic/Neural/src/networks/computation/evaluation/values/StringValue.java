package networks.computation.evaluation.values;

import networks.computation.evaluation.values.distributions.ValueInitializer;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;

/**
 * This is just an ad-hoc helper class for cases where it might make sense to pass a string through the values.
 *
 * Obviously no algebra implemented here.
 *
 * Created by gusta on 8.3.17.
 */
public class StringValue extends Value {

    String value;

    public StringValue(String valueText) {
        this.value = valueText;
    }

    @Override
    public void initialize(ValueInitializer valueInitializer) {

    }



    @Override
    public Value zero() {
        return null;
    }

    @Override
    public Value clone() {
        return null;
    }

    @Override
    public Value getForm() {
        return null;
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
        return null;
    }

    @Override
    public Value times(ScalarValue value) {
        return null;
    }

    @Override
    public Value times(VectorValue value) {
        return null;
    }

    @Override
    public Value times(MatrixValue value) {
        return null;
    }

    @Override
    public Value plus(Value value) {
        return null;
    }

    @Override
    public Value plus(ScalarValue value) {
        return null;
    }

    @Override
    public Value plus(VectorValue value) {
        return null;
    }

    @Override
    public Value plus(MatrixValue value) {
        return null;
    }

    @Override
    public Value minus(Value value) {
        return null;
    }

    @Override
    public Value minus(ScalarValue value) {
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

    @NotNull
    @Override
    public Iterator<Double> iterator() {
        return null;
    }
}