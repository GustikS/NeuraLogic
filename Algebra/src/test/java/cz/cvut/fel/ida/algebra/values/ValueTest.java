package cz.cvut.fel.ida.algebra.values;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ValueTest {

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
    public void compareToAllMixed(){
        Value small = new ScalarValue(2.0);
        VectorValue big = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));

        int compareTo = big.compareTo(small);
        assertEquals(0, compareTo);
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

}