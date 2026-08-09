package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bound has to be {@code 1/sqrt(fan_in)}, which is what {@code torch.nn.Linear} draws from and what
 * {@code torch.nn.RNN} draws its square recurrent weight from. Drawing enough samples to see the edge of the
 * range is the only way to check a distribution from outside, so these ask for the extremes of a few thousand.
 */
class TorchUniformInitializerTest {

    private static final int DRAWS = 20000;

    private static Settings settings() {
        Settings settings = new Settings();
        settings.initializer = Settings.InitSet.TORCH;
        settings.infer();
        return settings;
    }

    /**
     * The widest and narrowest value seen over a matrix of that many entries, as a fraction of the bound.
     */
    private static double observedBound(int rows, int cols) {
        MatrixValue matrix = new MatrixValue(rows, cols);
        new TorchUniformInitializer(settings()).initMatrix(matrix);

        double widest = 0;
        for (double value : matrix.values) {
            widest = Math.max(widest, Math.abs(value));
        }
        return widest;
    }

    @TestAnnotations.Fast
    public void matrixIsDrawnFromOneOverRootOfItsColumns() {
        int cols = 64;
        double expected = 1.0 / Math.sqrt(cols);

        double observed = observedBound(DRAWS / cols, cols);

        assertTrue(observed <= expected, "drew " + observed + ", outside the bound " + expected);
        assertTrue(observed > expected * 0.99, "drew only " + observed + ", far inside the bound " + expected);
    }

    /**
     * The columns are what the weight consumes, so a wide input narrows the range and a tall output does not.
     */
    @TestAnnotations.Fast
    public void onlyTheColumnsDecideTheRange() {
        double wide = observedBound(4, 256);
        double tall = observedBound(256, 4);

        assertEquals(1.0 / Math.sqrt(256), wide, 1e-3);
        assertEquals(1.0 / Math.sqrt(4), tall, 1e-3);
    }

    private static double observedBound(VectorValue vector) {
        new TorchUniformInitializer(settings()).initVector(vector);

        double widest = 0;
        for (double value : vector.values) {
            widest = Math.max(widest, Math.abs(value));
        }
        return widest;
    }

    /**
     * A row vector was declared {@code (1,n)} and consumes all n, the {@code torch.nn.Linear(n, 1)} case.
     */
    @TestAnnotations.Fast
    public void rowVectorIsDrawnFromOneOverRootOfItsLength() {
        double expected = 1.0 / Math.sqrt(DRAWS);

        assertEquals(expected, observedBound(new VectorValue(DRAWS, true)), expected * 0.01);
    }

    /**
     * A column vector was declared {@code (n,1)}, or as a plain {@code (n)}, and consumes one - so it keeps
     * the full range, exactly as {@code torch.nn.Linear(1, n)} does. Narrowing this one too costs
     * {@code test_xor_generalization}, whose weights are {@code (8,1)}, its convergence.
     */
    @TestAnnotations.Fast
    public void columnVectorKeepsTheFullRange() {
        assertEquals(1.0, observedBound(new VectorValue(DRAWS, false)), 0.01);
    }

    /**
     * One input, so the bound is one - the same {@code torch.nn.Linear(1, 1)} gets, and notably not what
     * {@link GlorotUniformInitializer} gives a scalar, which is {@code sqrt(6)/sqrt(2)} and so wider than the
     * previous default rather than narrower.
     */
    @TestAnnotations.Fast
    public void scalarIsDrawnFromMinusOneToOne() {
        double widest = 0;
        TorchUniformInitializer initializer = new TorchUniformInitializer(settings());
        for (int i = 0; i < DRAWS; i++) {
            ScalarValue scalar = new ScalarValue();
            initializer.initScalar(scalar);
            widest = Math.max(widest, Math.abs(scalar.value));
        }
        assertEquals(1.0, widest, 0.01);
    }

    @TestAnnotations.Fast
    public void theFactoryHandsBackThisOneWhenAskedFor() {
        assertTrue(ValueInitializer.getInitializer(settings()) instanceof TorchUniformInitializer);
    }
}
