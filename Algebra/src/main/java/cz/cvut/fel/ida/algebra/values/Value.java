package cz.cvut.fel.ida.algebra.values;

import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.setup.Settings;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.function.DoubleUnaryOperator;
import java.util.logging.Logger;

/**
 * Custom algebraic objects with standard semantics, i.e. scalar, vector and matrix with corresponding operations.
 * By default all the operations are considered as element-wise wherever possible.
 *
 * @implements Iterable which is meant to return the underlying elementary values in a predefined order, i.e. a serialization.
 * <p>
 * Created by gusta on 8.3.17.
 */
public abstract class Value implements Iterable<Double>, Comparable<Value>, Serializable {   //todo add division
    private static final Logger LOG = Logger.getLogger(Value.class.getName());

    /**
     * Random elementary values
     *
     * @param valueInitializer
     */
    public abstract void initialize(ValueInitializer valueInitializer);

    /**
     * Zero elementary values
     *
     * @return
     */
    public abstract Value zero();

    /**
     * Deep copy
     *
     * @return
     */
    public abstract Value clone();

    /**
     * Equivalent to clone() + zero(), but faster
     *
     * @return
     */
    public abstract Value getForm();

    /**
     * Transpose this Value
     *
     * @return
     */
    public abstract void transpose();

    /**
     * Returns a new Value object as a transposed view of the same data
     *
     * @return
     */
    public abstract Value transposedView();

    /**
     * Dimension representation up to a 2D matrix
     *
     * @return
     */
    public abstract int[] size();

    /** Get a slice of the value
     *
     * @param rows
     * @param cols
     * @return
     */
    public abstract Value slice(int[] rows, int[] cols);

    /** Reshapes the value
     *
     * @param shape
     * @return
     */
    public abstract Value reshape(int[] shape);

    /**
     * Get the value representation as double array
     *
     * @return
     */
    public abstract double[] getAsArray();

    /**
     * Set the value representation from double array
     *
     * @param value
     */
    public abstract void setAsArray(double[] value);

    /**
     * Element-wise application of a given real function
     *
     * @param function
     * @return
     */
    public abstract Value apply(DoubleUnaryOperator function);

    /**
     * Element-wise application of a given real function
     *
     * @param function
     */
    public abstract void applyInplace(DoubleUnaryOperator function);

    /**
     * Since Value is Iterable<Double>, we can access i-th element
     *
     * @param i
     * @return
     */
    public abstract double get(int i);

    /**
     * Set i-th element
     *
     * @param i
     * @param value
     */
    public abstract void set(int i, double value);

    /**
     * A faster shortcut to get+set for incrementation of particular element
     *
     * @param i
     * @param value
     */
    public abstract void increment(int i, double value);

    /**
     * Print out values with certain decimal precision
     *
     * @param numberFormat
     * @return
     */
    public abstract String toString(NumberFormat numberFormat);

    /**
     * Printout values with default (short) number precision
     * @return
     */
    @Override
    public String toString() {
        return toString(Settings.defaultNumberFormat);
    }

    /**
     * Can be used for debugging
     *
     * @return
     */
    public String toDetailedString() {
        return toString(Settings.superDetailedNumberFormat);
    }

    /**
     * CONSTRUCTIVE multiplication, i.e. creation of a NEW Value under the hood to be returned.
     * <p>
     * Uses double-dispatch with switch of the left-right hand sides to keep in mind!
     *
     * @param value
     * @return
     */
    public abstract Value times(Value value);

    protected abstract Value times(ScalarValue value);  //always keep the specific dispatch methods protected from outside calls so that it always goes correctly through the double dispatch as expected

    protected abstract Value times(VectorValue value);

    protected abstract Value times(MatrixValue value);

    protected abstract Value times(TensorValue value);


    /**
     * ELEMENT-wise product - CONSTRUCTIVE
     *
     * @param value
     * @return
     */
    public abstract Value elementTimes(Value value);

    protected abstract Value elementTimes(ScalarValue value);

    protected abstract Value elementTimes(VectorValue value);

    protected abstract Value elementTimes(MatrixValue value);

    protected abstract Value elementTimes(TensorValue value);


    /**
     * CONSTRUCTIVE transposition+multiplication within one call (for optimization)
     *
     * @param value
     * @return
     */
    public abstract Value transposedTimes(Value value);

    protected abstract Value transposedTimes(ScalarValue value);

    protected abstract Value transposedTimes(VectorValue value);

    protected abstract Value transposedTimes(MatrixValue value);

    protected abstract Value transposedTimes(TensorValue value);

    /**
     * KRONECKER product - CONSTRUCTIVE
     *
     * @param value
     * @return
     */
    public abstract Value kroneckerTimes(Value value);

    protected abstract Value kroneckerTimes(ScalarValue value);

    protected abstract Value kroneckerTimes(VectorValue value);

    protected abstract Value kroneckerTimes(MatrixValue value);

    protected abstract Value kroneckerTimes(TensorValue value);


    /**
     * ELEMENT-wise division - CONSTRUCTIVE
     *
     * @param value
     * @return
     */
    public abstract Value elementDivideBy(Value value);

    protected abstract Value elementDivideBy(ScalarValue value);

    protected abstract Value elementDivideBy(VectorValue value);

    protected abstract Value elementDivideBy(MatrixValue value);

    protected abstract Value elementDivideBy(TensorValue value);

    /**
     * CONSTRUCTIVE adding - will create a new Value.
     *
     * @param value
     * @return
     */
    public abstract Value plus(Value value);

    protected abstract Value plus(ScalarValue value);

    protected abstract Value plus(VectorValue value);

    protected abstract Value plus(MatrixValue value);

    protected abstract Value plus(TensorValue value);

    /**
     * CONSTRUCTIVE subtracting - will create a new Value.
     *
     * @param value
     * @return
     */
    public abstract Value minus(Value value);   //todo this must be element-wise!

    protected abstract Value minus(ScalarValue value);

    protected abstract Value minus(VectorValue value);

    protected abstract Value minus(MatrixValue value);

    protected abstract Value minus(TensorValue value);

    /**
     * DESTRUCTIVE adding - changes the INPUT Value.
     * - faster as there is no need to create new Value Object.
     *
     * @param value -THIS ONE WILL GET INCREMENTED BY THE CALLER!!
     * @return
     */
    public abstract void incrementBy(Value value);

    protected abstract void incrementBy(ScalarValue value);

    protected abstract void incrementBy(VectorValue value);

    protected abstract void incrementBy(MatrixValue value);

    protected abstract void incrementBy(TensorValue value);


    /**
     * DESTRUCTIVE multiplication - changes the INPUT Value.
     * - faster as there is no need to create new Value Object.
     *
     * @param value -THIS ONE WILL GET INCREMENTED BY THE CALLER!!
     * @return
     */
    public abstract void elementMultiplyBy(Value value);

    protected abstract void elementMultiplyBy(ScalarValue value);

    protected abstract void elementMultiplyBy(VectorValue value);

    protected abstract void elementMultiplyBy(MatrixValue value);

    protected abstract void elementMultiplyBy(TensorValue value);


    /**
     * Comparison with ad-hoc semantics (based on majority) for Values of different dimensions.
     *
     * @param maxValue
     * @return
     */
    public abstract boolean greaterThan(Value maxValue);    //todo add GTE version too

    protected abstract boolean greaterThan(ScalarValue maxValue);

    protected abstract boolean greaterThan(VectorValue maxValue);

    protected abstract boolean greaterThan(MatrixValue maxValue);

    protected abstract boolean greaterThan(TensorValue maxValue);

    public abstract int hashCode();

    public abstract boolean equals(Value obj);

    @Override
    public int compareTo(Value o) {
        if (greaterThan(o)) {
            return 1;
        } else if (o.greaterThan(this)) {   //this shouldn't depend on equals() ! as that means something different (use for Maps etc.)...
            return -1;
        } else {
            return 0;   // this also covers mixed cases such as ScalarValue <=all=> values in VectorValue
        }
    }

    public int getMaxInd() {
        Iterator<Double> iterator = iterator();
        double max = Double.NEGATIVE_INFINITY;
        int maxInd = -1;
        int i = 0;
        while (iterator.hasNext()) {
            Double next = iterator.next();
            if (next > max) {
                maxInd = i; //get the index of the target class (assuming one-hot encoding)
                max = next;
            }
            i++;
        }
        return maxInd;
    }

    //----------todo consider replacing these constant classes with simple ScalarValue(0/1), the speedup might be small.

    public static final Value ZERO = new Zero();
    public static final Value ONE = new One();

}