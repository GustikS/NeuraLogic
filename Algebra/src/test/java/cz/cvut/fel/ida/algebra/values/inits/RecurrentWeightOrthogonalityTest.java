package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Orthonormal rows are not an initializer to pick but something a weight is drawn with because of where it
 * sits - {@link Weight#onRecurrentRule}. So what is asserted is that the property arrives on the weights a
 * recursive rule applies and nowhere else, and that it is the property rather than the drawing.
 */
class RecurrentWeightOrthogonalityTest {

    private static final double TOLERANCE = 1e-9;

    private static Settings settings() {
        Settings settings = new Settings();
        settings.infer();
        return settings;
    }

    private static MatrixValue drawn(int rows, int cols, boolean recurrent) {
        Weight weight = new Weight(0, "w", new MatrixValue(rows, cols), false, false);
        weight.onRecurrentRule = recurrent;

        MatrixValue matrix = (MatrixValue) weight.value;
        new GlorotUniformInitializer(settings()).forWeight(weight).initMatrix(matrix);
        return matrix;
    }

    private static double dot(MatrixValue matrix, int first, int second) {
        double total = 0;
        for (int k = 0; k < matrix.cols; k++) {
            total += matrix.values[first * matrix.cols + k] * matrix.values[second * matrix.cols + k];
        }
        return total;
    }

    @TestAnnotations.Fast
    public void aWeightOnARecursiveRuleComesOutOrthonormal() {
        MatrixValue matrix = drawn(16, 16, true);

        for (int i = 0; i < 16; i++) {
            assertEquals(1.0, dot(matrix, i, i), TOLERANCE, "row " + i + " is not unit length");
            for (int j = i + 1; j < 16; j++) {
                assertEquals(0.0, dot(matrix, i, j), TOLERANCE, i + " and " + j + " are not orthogonal");
            }
        }
    }

    /**
     * Everywhere else the fan scaling is what is wanted, and orthonormal rows would replace it rather than
     * add to it - so a weight applied once keeps the draw it always had.
     */
    @TestAnnotations.Fast
    public void anOrdinaryWeightIsLeftAsItWas() {
        MatrixValue matrix = drawn(16, 16, false);

        boolean anyRowIsUnitLength = false;
        for (int i = 0; i < 16; i++) {
            anyRowIsUnitLength |= Math.abs(dot(matrix, i, i) - 1.0) < 1e-6;
        }
        assertTrue(!anyRowIsUnitLength, "a plain Glorot draw should not come out orthonormal by accident");
    }

    /**
     * A rectangular weight is left alone even on a recursive rule: orthonormal rows and a fan-in scale answer
     * different questions, and only for a square weight is norm preservation the one worth answering.
     */
    @TestAnnotations.Fast
    public void aRectangularWeightIsLeftAsItWas() {
        MatrixValue matrix = drawn(4, 16, true);
        double expected = Math.sqrt(6) / Math.sqrt(4 + 16);

        for (double value : matrix.values) {
            assertTrue(Math.abs(value) <= expected, "drew " + value + ", outside the Glorot bound " + expected);
        }
    }

    /**
     * The property this exists for, over the depth where it matters: sixteen applications, same length.
     */
    @TestAnnotations.Fast
    public void lengthSurvivesManyApplications() {
        MatrixValue matrix = drawn(16, 16, true);
        double[] state = new double[16];
        java.util.Arrays.fill(state, 0.25);
        double before = Math.sqrt(16 * 0.25 * 0.25);

        for (int step = 0; step < 16; step++) {
            double[] next = new double[16];
            for (int row = 0; row < 16; row++) {
                for (int col = 0; col < 16; col++) {
                    next[row] += matrix.values[row * 16 + col] * state[col];
                }
            }
            state = next;
        }

        double after = 0;
        for (double value : state) {
            after += value * value;
        }
        assertEquals(before, Math.sqrt(after), TOLERANCE);
    }

    /**
     * The activation correction still applies on top, so the rows come out that long rather than unit.
     */
    @TestAnnotations.Fast
    public void theActivationGainStillScalesIt() {
        Weight weight = new Weight(0, "w", new MatrixValue(8, 8), false, false);
        weight.onRecurrentRule = true;
        weight.setActivationGain(5.0 / 3);

        MatrixValue matrix = (MatrixValue) weight.value;
        new GlorotUniformInitializer(settings()).forWeight(weight).initMatrix(matrix);

        assertEquals(5.0 / 3, Math.sqrt(dot(matrix, 0, 0)), TOLERANCE);
    }

    /**
     * Torch scaling and orthogonal recurrence are separate choices, so asking for one has to leave the other.
     */
    @TestAnnotations.Fast
    public void itComposesWithWhicheverInitializerIsInUse() {
        Weight weight = new Weight(0, "w", new MatrixValue(8, 8), false, false);
        weight.onRecurrentRule = true;

        MatrixValue matrix = (MatrixValue) weight.value;
        new TorchUniformInitializer(settings()).forWeight(weight).initMatrix(matrix);

        assertEquals(1.0, dot(matrix, 0, 0), TOLERANCE);
    }
}
