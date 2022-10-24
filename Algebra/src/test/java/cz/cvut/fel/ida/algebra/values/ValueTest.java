package cz.cvut.fel.ida.algebra.values;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ValueTest {

    @TestAnnotations.Fast
    public void iteration() {
        Value v1 = new MatrixValue(new double[]{1.0, 2.0, 2.0, 4, 3, 6}, 3, 2);
        Iterator<Double> iterator = v1.iterator();
        Double aDouble = null;
        while (iterator.hasNext()) {
            aDouble = iterator.next();
            System.out.println(aDouble);
        }
        assertEquals(aDouble, 6);
    }


    @TestAnnotations.Fast
    public void wrongTransposition() {
        Value v1 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        VectorValue v2 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        assertThrows(ArithmeticException.class, () -> v1.times(v2));
    }

    @TestAnnotations.Fast
    public void correctDimensions() {
        Value v1 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        double contents[] = {1.0, 2.0, 3.0};
        Value m1 = new MatrixValue(contents, 1, 3);
        assertNotNull(m1.times(v1));
    }

    /**
     * It calls the correct default method of {@link MatrixValue#times(Value)}
     */
    @TestAnnotations.Fast
    public void correctDoubleDispatch() {
        Value v1 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        double contents[] = {1.0, 2.0, 3.0};
        MatrixValue m1 = new MatrixValue(contents, 1, 3);
        assertNotNull(m1.times(v1));
    }

    /**
     * It calls wrong method without DD {link MatrixValue#times(VectorValue)}
     */
    @TestAnnotations.Fast
    public void dynamicDispatchTrap() {
        VectorValue v1 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        double contents[] = {1.0, 2.0, 3.0};
        Value m1 = new MatrixValue(contents, 1, 3);
        assertThrows(ArithmeticException.class, () -> m1.times(v1));
    }

    @TestAnnotations.Fast
    public void compareTo() {
        Value small = new ScalarValue(-3);
        ScalarValue big = new ScalarValue(10);

        int compareTo = small.compareTo(big);
        assertEquals(compareTo, -1);
    }

    @TestAnnotations.Fast
    public void compareToSame() {
        Value small = new ScalarValue(10);
        ScalarValue big = new ScalarValue(10);

        int compareTo = small.compareTo(big);
        assertEquals(compareTo, 0);
    }

    @TestAnnotations.Fast
    public void compareToAll() {
        Value small = new ScalarValue(10);
        VectorValue big = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));

        int compareTo = small.compareTo(big);
        assertEquals(compareTo, 1);
    }

    @TestAnnotations.Fast
    public void compareToAllsmaller() {
        Value small = new ScalarValue(0.5);
        VectorValue big = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));

        int compareTo = small.compareTo(big);
        assertEquals(compareTo, -1);
    }

    @TestAnnotations.Fast
    public void compareToAllsmallerOposite() {
        Value small = new ScalarValue(0.5);
        VectorValue big = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));

        int compareTo = big.compareTo(small);
        assertEquals(compareTo, 1);
    }

    @TestAnnotations.Fast
    public void compareToAllMixed() {
        Value small = new ScalarValue(2.0);
        VectorValue big = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));

        int compareTo = big.compareTo(small);
        assertEquals(1, compareTo);
    }

    @TestAnnotations.Fast
    public void equals() {
        Value small = new ScalarValue(10);
        ScalarValue big = new ScalarValue(10);

        boolean compareTo = small.equals(big);
        assertTrue(compareTo);
    }

    @TestAnnotations.Fast
    public void equalsNot() {
        Value small = new ScalarValue(10);
        ScalarValue big = new ScalarValue(2);

        boolean compareTo = small.equals(big);
        assertFalse(compareTo);
    }

    @TestAnnotations.Fast
    public void equalsWrong() {
        Value small = new ScalarValue(10);
        VectorValue big = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));

        boolean compareTo = small.equals(big);
        assertFalse(compareTo);
    }

    @TestAnnotations.Fast
    public void kroneckerVectorVector1() {
        Value a = new VectorValue(Arrays.asList(1.0, 2.0));
        Value b = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));

        Value kroneckerTimes = a.kroneckerTimes(b);
        assertEquals(new VectorValue(new double[]{1.0, 2.0, 3.0, 2, 4, 6}, false), kroneckerTimes);
    }

    @TestAnnotations.Fast
    public void kroneckerVectorVector2() {
        Value a = new VectorValue(new double[]{1.0, 2.0}, true);
        Value b = new VectorValue(new double[]{1.0, 2.0, 3.0}, true);

        Value kroneckerTimes = a.kroneckerTimes(b);
        assertEquals(new VectorValue(new double[]{1.0, 2.0, 3.0, 2, 4, 6}, true), kroneckerTimes);
    }

    @TestAnnotations.Fast
    public void kroneckerVectorVector3() {
        Value a = new VectorValue(new double[]{1.0, 2.0}, true);
        Value b = new VectorValue(new double[]{1.0, 2.0, 3.0}, false);

        Value kroneckerTimes = a.kroneckerTimes(b);
        boolean equals = new MatrixValue(new double[]{1.0, 2.0, 2.0, 4, 3, 6}, 3, 2).equals(kroneckerTimes);
        assertTrue(equals);
    }

    @TestAnnotations.Fast
    public void kroneckerVectorVector4() {
        Value a = new VectorValue(new double[]{1.0, 2.0}, false);
        Value b = new VectorValue(new double[]{1.0, 2.0, 3.0}, true);

        Value kroneckerTimes = a.kroneckerTimes(b);
        boolean equals = new MatrixValue(new double[]{1.0, 2.0, 2.0, 4, 3, 6}, 3, 2).transposedView().equals(kroneckerTimes);
        assertTrue(equals);
    }

    @TestAnnotations.Fast
    public void kroneckerVectorMatrix1() {
        Value a = new VectorValue(new double[]{1.0, 2.0}, false);
        Value b = new MatrixValue(new double[]{1.0, 2.0, 3.0, 4.0}, 2, 2);

        Value kroneckerTimes = a.kroneckerTimes(b);
        boolean equals = new MatrixValue(new double[]{1.0, 2.0, 3, 4, 2, 4, 6, 8}, 4, 2).equals(kroneckerTimes);
        assertTrue(equals);
    }

    @TestAnnotations.Fast
    public void kroneckerVectorMatrix2() {
        Value a = new VectorValue(new double[]{1.0, 2.0, 3.0}, true);
        Value b = new MatrixValue(new double[]{1.0, 2.0, 3.0, 4.0}, 2, 2);

        Value kroneckerTimes = a.kroneckerTimes(b);
        boolean equals = new MatrixValue(new double[]{1.0, 2.0, 2, 4, 3, 6, 3, 4, 6, 8, 9, 12}, 2, 6).equals(kroneckerTimes);
        assertTrue(equals);
    }

    @TestAnnotations.Fast
    public void kroneckerMatrixVector1() {
        Value a = new VectorValue(new double[]{1.0, 2.0, 3.0}, true);
        Value b = new MatrixValue(new double[]{1.0, 2.0, 3.0, 4.0}, 2, 2);

        Value kroneckerTimes = b.kroneckerTimes(a);
        boolean equals = new MatrixValue(new double[]{1.0, 2.0, 3, 2, 4, 6, 3, 6, 9, 4, 8, 12}, 2, 6).equals(kroneckerTimes);
        assertTrue(equals);
    }

    @TestAnnotations.Fast
    public void kroneckerMatrixVector2() {
        Value a = new VectorValue(new double[]{1.0, 2.0, 3.0}, false);
        Value b = new MatrixValue(new double[]{1.0, 2.0, 3.0, 4.0}, 2, 2);

        Value kroneckerTimes = b.kroneckerTimes(a);
        boolean equals = new MatrixValue(new double[]{1.0, 2.0, 2, 4, 3, 6, 3, 4, 6, 8, 9, 12}, 6, 2).equals(kroneckerTimes);
        assertTrue(equals);
    }

    @TestAnnotations.Fast
    public void kroneckerMatrixMatrix1() {
        Value a = new MatrixValue(new double[]{1.0, 2.0, 3.0, 3.0, 4.0, 5.0}, 2, 3);
        Value b = new MatrixValue(new double[]{1.0, 2.0, 3.0, 4.0}, 2, 2);

        Value kroneckerTimes = a.kroneckerTimes(b);
        boolean equals = new MatrixValue(
            new double[]{1.0, 2.0, 2.0, 4.0, 3.0, 6.0, 3.0, 4.0, 6.0, 8.0, 9.0, 12.0, 3.0, 6.0, 4.0, 8.0, 5.0, 10.0, 9.0, 12.0, 12.0, 16.0, 15.0, 20.0}, 4, 6
        ).equals(kroneckerTimes);
        assertTrue(equals);
    }

    @TestAnnotations.Fast
    public void matrixGradient() {
        Value v = new MatrixValue(new double[]{1.2, 2.07, 3.67, 3.25, 4.1, 5.7}, 2, 3);
        Value w = new VectorValue(new double[]{3.2, 4.76},true);

        Value g = new VectorValue(new double[]{1.2, 2.11, 3.02}, true);


        Value output = w.times(v);
        System.out.println(output);

        Value grad1 = g.transposedView().times(w).transposedView();
        System.out.println(grad1);

        Value grad2 = w.transposedView().times(g);
        System.out.println(grad2);

        assertTrue(grad1.equals(grad2));
    }


    @TestAnnotations.Fast
    public void transposedTimesMatrixMatrix() {
        Value a = new MatrixValue(new double[]{1.0, 2.0, 3.0, 3.0, 4.0, 5.0}, 2, 3);
        Value b = new MatrixValue(new double[]{1.0, 2.0, 3.5, 3.5, 3.0, 4.0, 3.5, 3.5}, 2, 4);

        Value c = a.transposedTimes(b);
        Value d = a.transposedView().times(b);

        assertTrue(c.equals(d));
    }

    @TestAnnotations.Fast
    public void transposedTimesMatrixVector() {
        Value a = new MatrixValue(new double[]{1.0, 2.0, 3.0, 3.0, 4.0, 5.0}, 2, 3);
        Value b = new VectorValue(new double[]{1.0, 2.0});

        Value c = a.transposedTimes(b);
        Value d = a.transposedView().times(b);

        assertTrue(c.equals(d));
    }


    @TestAnnotations.Fast
    public void transposedTimesVectorMatrix() {
        Value a = new VectorValue(new double[]{1.0, 2.0});
        Value b = new MatrixValue(new double[]{1.0, 2.0, 3.0, 3.0, 4.0, 5.0}, 2, 3);

        Value c = a.transposedTimes(b);
        Value d = a.transposedView().times(b);

        assertTrue(c.equals(d));
    }

    @TestAnnotations.Fast
    public void transposedTimesScalarMatrix() {
        Value a = new ScalarValue(0.5);
        Value b = new MatrixValue(new double[]{1.0, 2.0, 3.0, 3.0, 4.0, 5.0}, 2, 3);

        Value c = a.transposedTimes(b);
        Value d = a.transposedView().times(b);

        assertTrue(c.equals(d));
    }

    @TestAnnotations.Fast
    public void transposedTimesMatrixScalar() {
        Value b = new MatrixValue(new double[]{1.0, 2.0, 3.0, 3.0, 4.0, 5.0}, 2, 3);
        Value a = new Zero();

        Value c = a.transposedTimes(b);
        Value d = a.transposedView().times(b);

        assertTrue(c.equals(d));
    }

    @TestAnnotations.Fast
    public void compare(){
        Value a = new VectorValue(Arrays.asList(1.0,2.0,6.0));
        Value b = new VectorValue(Arrays.asList(2.0,2.0,3.0));
        Value c = new VectorValue(Arrays.asList(2.0,2.0,6.0));

        assertEquals(1,a.compareTo(b));
        assertEquals(-1,b.compareTo(c));
        assertEquals(-1,a.compareTo(c));
    }
}