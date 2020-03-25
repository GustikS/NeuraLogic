package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.Arrays;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ValueTest {
    private static final Logger LOG = Logger.getLogger(ValueTest.class.getName());

    @TestAnnotations.Fast
    public void init() {
        Value scalar = new ScalarValue(1);
        VectorValue vector = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        Value times = scalar.times(vector);
        LOG.fine(times.toString());
    }

    @TestAnnotations.Fast
    public void wrongTransposition() {
        Value v1 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        VectorValue v2 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
//        assertThrows(java.lang.NumberFormatException, v1.times(v2));
        assertNull(v1.times(v2));
    }

    @TestAnnotations.Fast
    public void correctDimensions() {
        Value v1 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        double contents[][] = {{1.0, 2.0, 3.0}};
        Value m1 = new MatrixValue(contents);
        assertNotNull(m1.times(v1));
    }

    /**
     * It calls the correct default method of {@link MatrixValue#times(Value)}
     */
    @TestAnnotations.Fast
    public void correctDoubleDispatch() {
        Value v1 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        double contents[][] = {{1.0, 2.0, 3.0}};
        MatrixValue m1 = new MatrixValue(contents);
        assertNotNull(m1.times(v1));
    }

    /**
     * It still calls the correct method thanks to the PROTECTED modifier of {link MatrixValue#times(VectorValue)}
     */
    @TestAnnotations.Fast
    public void dynamicDispatchTrap() {
        VectorValue v1 = new VectorValue(Arrays.asList(1.0, 2.0, 3.0));
        double contents[][] = {{1.0, 2.0, 3.0}};
        Value m1 = new MatrixValue(contents);
        assertNotNull(m1.times(v1));
    }
}