package cz.cvut.fel.ida.algebra.values;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;

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
    public void transpose() {

    }

    @Override
    public Value transposedView() {
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
    public double get(int i) {
        return 0;
    }

    @Override
    public void set(int i, double value) {

    }

    @Override
    public void increment(int i, double value) {

    }

    @Override
    public String toString() {
        return value;
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
    protected Value times(TensorValue value) {
        return null;
    }

    @Override
    public Value elementTimes(Value value) {
        return null;
    }

    @Override
    protected Value elementTimes(ScalarValue value) {
        return null;
    }

    @Override
    protected Value elementTimes(VectorValue value) {
        return null;
    }

    @Override
    protected Value elementTimes(MatrixValue value) {
        return null;
    }

    @Override
    protected Value elementTimes(TensorValue value) {
        return null;
    }

    @Override
    public Value kroneckerTimes(Value value) {
        return null;
    }

    @Override
    protected Value kroneckerTimes(ScalarValue value) {
        return null;
    }

    @Override
    protected Value kroneckerTimes(VectorValue value) {
        return null;
    }

    @Override
    protected Value kroneckerTimes(MatrixValue value) {
        return null;
    }

    @Override
    protected Value kroneckerTimes(TensorValue value) {
        return null;
    }

    @Override
    public Value elementDivideBy(Value value) {
        return null;
    }

    @Override
    protected Value elementDivideBy(ScalarValue value) {
        return null;
    }

    @Override
    protected Value elementDivideBy(VectorValue value) {
        return null;
    }

    @Override
    protected Value elementDivideBy(MatrixValue value) {
        return null;
    }

    @Override
    protected Value elementDivideBy(TensorValue value) {
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
    protected Value plus(TensorValue value) {
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
    protected Value minus(TensorValue value) {
        return null;
    }

    @Override
    public void incrementBy(Value value) {

    }

    @Override
    public void incrementBy(ScalarValue value) {

    }

    @Override
    public void incrementBy(VectorValue value) {

    }

    @Override
    public void incrementBy(MatrixValue value) {

    }

    @Override
    protected void incrementBy(TensorValue value) {

    }

    @Override
    public void elementMultiplyBy(Value value) {

    }

    @Override
    protected void elementMultiplyBy(ScalarValue value) {

    }

    @Override
    protected void elementMultiplyBy(VectorValue value) {

    }

    @Override
    protected void elementMultiplyBy(MatrixValue value) {

    }

    @Override
    protected void elementMultiplyBy(TensorValue value) {

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

    @Override
    protected boolean greaterThan(TensorValue maxValue) {
        return false;
    }

    @Override
    public boolean equals(Value obj) {
        if (obj instanceof StringValue){
            if (value.equals(((StringValue) obj).value)){
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<Double> iterator() {
        return null;
    }
}