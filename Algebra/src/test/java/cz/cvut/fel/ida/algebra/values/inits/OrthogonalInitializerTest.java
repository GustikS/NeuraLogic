package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The property is what is being asserted, not the drawing: the vectors have to be orthonormal, and a vector
 * put through the matrix has to come out the same length. Anything short of that leaves the point of using
 * it - a weight applied over and over not changing the size of what flows through - unestablished.
 */
class OrthogonalInitializerTest {

    private static final double TOLERANCE = 1e-9;

    private static Settings settings() {
        Settings settings = new Settings();
        settings.initializer = Settings.InitSet.ORTHOGONAL;
        settings.infer();
        return settings;
    }

    private static MatrixValue drawn(int rows, int cols) {
        MatrixValue matrix = new MatrixValue(rows, cols);
        new OrthogonalInitializer(settings()).initMatrix(matrix);
        return matrix;
    }

    private static double dot(MatrixValue matrix, int first, int second, boolean overRows) {
        int length = overRows ? matrix.cols : matrix.rows;
        int outer = overRows ? matrix.cols : 1;
        int inner = overRows ? 1 : matrix.cols;

        double total = 0;
        for (int k = 0; k < length; k++) {
            total += matrix.values[first * outer + k * inner] * matrix.values[second * outer + k * inner];
        }
        return total;
    }

    private static void assertOrthonormal(MatrixValue matrix, int count, boolean overRows) {
        for (int i = 0; i < count; i++) {
            assertEquals(1.0, dot(matrix, i, i, overRows), TOLERANCE, "vector " + i + " is not unit length");
            for (int j = i + 1; j < count; j++) {
                assertEquals(0.0, dot(matrix, i, j, overRows), TOLERANCE, i + " and " + j + " are not orthogonal");
            }
        }
    }

    @TestAnnotations.Fast
    public void aSquareMatrixComesOutOrthonormal() {
        assertOrthonormal(drawn(32, 32), 32, true);
    }

    /**
     * More rows than columns means the rows cannot all be orthogonal - there are more of them than there are
     * dimensions - so it is the columns that carry it.
     */
    @TestAnnotations.Fast
    public void aTallMatrixIsOrthonormalDownItsColumns() {
        assertOrthonormal(drawn(24, 8), 8, false);
    }

    @TestAnnotations.Fast
    public void aWideMatrixIsOrthonormalAcrossItsRows() {
        assertOrthonormal(drawn(8, 24), 8, true);
    }

    /**
     * The property this exists for, stated directly: length in equals length out, whatever the vector.
     */
    @TestAnnotations.Fast
    public void multiplyingByItLeavesALengthAlone() {
        MatrixValue matrix = drawn(16, 16);
        VectorValue input = new VectorValue(new double[]{
                0.3, -1.2, 0.7, 2.1, -0.4, 0.9, -1.7, 0.05, 1.1, -0.6, 0.2, -2.3, 1.4, 0.8, -0.9, 0.15});

        double before = 0;
        for (double value : input.values) {
            before += value * value;
        }

        double[] output = new double[16];
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                output[row] += matrix.values[row * 16 + col] * input.values[col];
            }
        }
        double after = 0;
        for (double value : output) {
            after += value * value;
        }

        assertEquals(Math.sqrt(before), Math.sqrt(after), 1e-9);
    }

    /**
     * Sixteen applications is where a Glorot draw's best and worst were measured 19x apart; here it has to
     * still be the length it started at.
     */
    @TestAnnotations.Fast
    public void andKeepsLeavingItAloneOverManyApplications() {
        MatrixValue matrix = drawn(16, 16);
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

        assertEquals(before, Math.sqrt(after), 1e-9);
    }

    @TestAnnotations.Fast
    public void theGainScalesItAndTheFactoryHandsItBack() {
        MatrixValue plain = drawn(8, 8);
        MatrixValue widened = new MatrixValue(8, 8);
        ((OrthogonalInitializer) new OrthogonalInitializer(settings()).withGain(5.0 / 3)).initMatrix(widened);

        double plainNorm = 0, widenedNorm = 0;
        for (int i = 0; i < 64; i++) {
            plainNorm += plain.values[i] * plain.values[i];
            widenedNorm += widened.values[i] * widened.values[i];
        }

        assertEquals(5.0 / 3, Math.sqrt(widenedNorm / plainNorm), 1e-9);
        assertTrue(ValueInitializer.getInitializer(settings()) instanceof OrthogonalInitializer);
    }
}
