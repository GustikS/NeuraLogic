package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.transformation.joint.Reshape;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.Slice;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class ReshapeTest {

    @TestAnnotations.Fast
    public void evalMatrixOneElementReshape() {
        MatrixValue matrix = new MatrixValue(new double[] { 1.0 }, 1, 1);

        Reshape reshape = new Reshape(new int[] { 0 });
        Value eval = reshape.evaluate(matrix);

        assertEquals(eval.getClass(), ScalarValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());

        reshape = new Reshape(null);
        eval = reshape.evaluate(matrix);

        assertEquals(eval.getClass(), ScalarValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());

        reshape = new Reshape(new int[] {0, 0});
        eval = reshape.evaluate(matrix);

        assertEquals(eval.getClass(), ScalarValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());

        reshape = new Reshape(new int[] {0, 1});
        eval = reshape.evaluate(matrix);

        assertEquals(eval.getClass(), VectorValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());
        assertFalse(((VectorValue) eval).rowOrientation);

        reshape = new Reshape(new int[] {1, 0});
        eval = reshape.evaluate(matrix);

        assertEquals(eval.getClass(), VectorValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());
        assertTrue(((VectorValue) eval).rowOrientation);
    }


    @TestAnnotations.Fast
    public void evalMatrixReshape() {
        MatrixValue matrix = new MatrixValue(new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 }, 2, 3);

        Reshape reshape = new Reshape(new int[] { 3, 2 });
        Value eval = reshape.evaluate(matrix);

        assertEquals(eval.getClass(), MatrixValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());
        assertEquals(((MatrixValue) eval).rows, 3);
        assertEquals(((MatrixValue) eval).cols, 2);

        reshape = new Reshape(new int[] { 1, 6 });
        eval = reshape.evaluate(matrix);

        assertEquals(eval.getClass(), MatrixValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());
        assertEquals(((MatrixValue) eval).rows, 1);
        assertEquals(((MatrixValue) eval).cols, 6);

        reshape = new Reshape(new int[] { 6, 1 });
        eval = reshape.evaluate(matrix);

        assertEquals(eval.getClass(), MatrixValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());
        assertEquals(((MatrixValue) eval).rows, 6);
        assertEquals(((MatrixValue) eval).cols, 1);

        reshape = new Reshape(new int[] { 0, 6 });
        eval = reshape.evaluate(matrix);

        assertEquals(eval.getClass(), VectorValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());
        assertFalse(((VectorValue) eval).rowOrientation);
    }

    @TestAnnotations.Fast
    public void evalVectorValueReshape() {
        VectorValue vector = new VectorValue(new double[] {0.5, 1.5, 2.0, 2.5});

        Reshape reshape = new Reshape(new int[] {2, 2});
        Value eval = reshape.evaluate(vector);

        assertEquals(eval.getClass(), MatrixValue.class);
        assertArrayEquals(eval.getAsArray(), vector.getAsArray());
        assertEquals(((MatrixValue) eval).rows, 2);
        assertEquals(((MatrixValue) eval).cols, 2);

        reshape = new Reshape(new int[] {1, 4});
        eval = reshape.evaluate(vector);

        assertEquals(eval.getClass(), MatrixValue.class);
        assertArrayEquals(eval.getAsArray(), vector.getAsArray());
        assertEquals(((MatrixValue) eval).rows, 1);
        assertEquals(((MatrixValue) eval).cols, 4);

        reshape = new Reshape(new int[] {0, 4});
        eval = reshape.evaluate(vector);

        assertEquals(eval.getClass(), VectorValue.class);
        assertArrayEquals(eval.getAsArray(), vector.getAsArray());
        assertFalse(((VectorValue) eval).rowOrientation);

        reshape = new Reshape(new int[] {4, 0});
        eval = reshape.evaluate(vector);

        assertEquals(eval.getClass(), VectorValue.class);
        assertArrayEquals(eval.getAsArray(), vector.getAsArray());
        assertTrue(((VectorValue) eval).rowOrientation);
    }
}
