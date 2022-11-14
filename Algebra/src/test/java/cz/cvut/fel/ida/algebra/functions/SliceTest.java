package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.transformation.joint.Slice;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SliceTest {

    @TestAnnotations.Fast
    public void evalScalarValue() {
        ScalarValue scalar = new ScalarValue(0.5);

        Slice slice = new Slice();
        Value eval = slice.evaluate(scalar);

        assertEquals(eval.getClass(), ScalarValue.class);
        assertArrayEquals(eval.getAsArray(), scalar.getAsArray());

        slice = new Slice(new int[] {0, 1}, new int[] {0, 1});
        eval = slice.evaluate(scalar);

        assertEquals(eval.getClass(), ScalarValue.class);
        assertArrayEquals(eval.getAsArray(), scalar.getAsArray());
    }

    @TestAnnotations.Fast
    public void evalVectorValue() {
        VectorValue vector = new VectorValue(new double[] {0.5, 1.5, 2.0, 2.5});

        Slice slice = new Slice();
        Value eval = slice.evaluate(vector);

        assertEquals(eval.getClass(), VectorValue.class);
        assertArrayEquals(eval.getAsArray(), vector.getAsArray());

        slice = new Slice(new int[] {0, 1}, new int[] {0, 1});
        eval = slice.evaluate(vector);

        assertEquals(eval.getClass(), VectorValue.class);
        assertArrayEquals(eval.getAsArray(), new double[] { 0.5 });

        slice = new Slice(new int[] {1, 3}, new int[] {0, 1});
        eval = slice.evaluate(vector);

        assertEquals(eval.getClass(), VectorValue.class);
        assertArrayEquals(eval.getAsArray(), new double[] { 1.5, 2.0 });
    }

    @TestAnnotations.Fast
    public void evalMatrixValue() {
        MatrixValue matrix = new MatrixValue(new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0}, 2, 4);

        Slice slice = new Slice();
        Value eval = slice.evaluate(matrix);

        assertEquals(eval.getClass(), MatrixValue.class);
        assertArrayEquals(eval.getAsArray(), matrix.getAsArray());

        slice = new Slice(new int[] {0, 1}, new int[] {0, 1});
        eval = slice.evaluate(matrix);

        assertEquals(eval.getClass(), MatrixValue.class);
        assertArrayEquals(eval.getAsArray(), new double[] { 0.0 });

        slice = new Slice(new int[] {1, 2}, null);
        eval = slice.evaluate(matrix);

        assertEquals(eval.getClass(), MatrixValue.class);
        assertArrayEquals(eval.getAsArray(), new double[] { 4.0, 5.0, 6.0, 7.0 });

        slice = new Slice(new int[] {1, 2}, new int[] {1, 3});
        eval = slice.evaluate(matrix);

        assertEquals(eval.getClass(), MatrixValue.class);
        assertArrayEquals(eval.getAsArray(), new double[] { 5.0, 6.0 });
    }
}
