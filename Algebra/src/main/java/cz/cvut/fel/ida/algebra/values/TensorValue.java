package cz.cvut.fel.ida.algebra.values;

import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.function.DoubleUnaryOperator;
import java.util.logging.Logger;

/**
 * todo finish this class once it is actually useful somewhere...(i.e. where matrix is not enough)
 */
public class TensorValue extends Value {
    private static final Logger LOG = Logger.getLogger(TensorValue.class.getName());

    /**
     * A multi-dimensional array wrapper
     */
    Tensor tensor;
    int[] dimensions;

    public TensorValue(int[] dimensions){
        tensor = new Tensor(dimensions);
        this.dimensions = dimensions;
    }

    @Override
    public void initialize(ValueInitializer valueInitializer) {

    }

    @Override
    public TensorValue zero() {
        for (int i = 0; i < tensor.values.length; i++) {
            tensor.values[i] = 0;
        }
        return this;
    }

    @Override
    public TensorValue clone() {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public TensorValue getForm() {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public void transpose() {

    }

    @Override
    public TensorValue transposedView() {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public int[] size() {
        return new int[0];
    }

    @Override
    public Value slice(int[] rows, int[] cols) {
        return null;
    }

    @Override
    public Value reshape(int[] shape) {
        return null;
    }

    @Override
    public double[] getAsArray() {
        return new double[0];
    }

    @Override
    public void setAsArray(double[] value) {}

    @Override
    public TensorValue apply(DoubleUnaryOperator function) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public void applyInplace(DoubleUnaryOperator function) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
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
    public String toString(NumberFormat numberFormat) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public Value times(Value value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value times(ScalarValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value times(VectorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value times(MatrixValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value times(TensorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public Value elementTimes(Value value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value elementTimes(ScalarValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value elementTimes(VectorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value elementTimes(MatrixValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value elementTimes(TensorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public Value transposedTimes(Value value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value transposedTimes(ScalarValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value transposedTimes(VectorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value transposedTimes(MatrixValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value transposedTimes(TensorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public Value kroneckerTimes(Value value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value kroneckerTimes(ScalarValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value kroneckerTimes(VectorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value kroneckerTimes(MatrixValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value kroneckerTimes(TensorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public Value elementDivideBy(Value value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value elementDivideBy(ScalarValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value elementDivideBy(VectorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value elementDivideBy(MatrixValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value elementDivideBy(TensorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public Value plus(Value value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value plus(ScalarValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value plus(VectorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value plus(MatrixValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value plus(TensorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public Value minus(Value value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value minus(ScalarValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value minus(VectorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value minus(MatrixValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    protected Value minus(TensorValue value) {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }

    @Override
    public void incrementBy(Value value) {

    }

    @Override
    protected void incrementBy(ScalarValue value) {

    }

    @Override
    protected void incrementBy(VectorValue value) {

    }

    @Override
    protected void incrementBy(MatrixValue value) {

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
    protected boolean greaterThan(ScalarValue maxValue) {
        return false;
    }

    @Override
    protected boolean greaterThan(VectorValue maxValue) {
        return false;
    }

    @Override
    protected boolean greaterThan(MatrixValue maxValue) {
        return false;
    }

    @Override
    protected boolean greaterThan(TensorValue maxValue) {
        return false;
    }

    @Override
    public int hashCode() {
        return tensor.hashCode();
    }

    @Override
    public boolean equals(Value obj) {
        if (obj instanceof TensorValue) {
            return this.tensor.equals(((TensorValue) obj).tensor);
        }
        return false;
    }

    @Override
    public Iterator<Double> iterator() {
        throw new ArithmeticException("Higher dimension Tensor operations are not implemented yet");
    }
}
