package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.distributions.Normal;

import java.util.logging.Logger;

/**
 * Draws a matrix whose rows are orthonormal, so that multiplying by it leaves the length of a vector alone
 * exactly rather than on average.
 * <p>
 * Only worth asking for on a weight a recursive rule applies over and over, which is the one place the
 * difference compounds - see {@link cz.cvut.fel.ida.algebra.weights.Weight#onRecurrentRule}. Modified rather
 * than classical Gram-Schmidt, because the whole point is a property holding exactly and the classical form
 * loses it to rounding as the dimension grows.
 */
public class Orthonormalisation {
    private static final Logger LOG = Logger.getLogger(Orthonormalisation.class.getName());

    /**
     * @param scale applied after orthonormalising, so the rows come out with that length rather than one
     */
    public static void fill(MatrixValue matrix, Normal gaussian, double scale) {
        final int rows = matrix.rows;
        final int cols = matrix.cols;
        final double[] values = matrix.values;

        for (int i = 0; i < values.length; i++) {
            values[i] = gaussian.getDoubleValue();
        }

        //orthonormalise whichever of the two there are fewer of - with more vectors than dimensions no set of
        //them can be orthogonal, so the shorter side is the one that can carry the property
        if (rows <= cols) {
            orthonormalise(values, rows, cols, cols, 1);     //row i starts at i*cols and steps by one
        } else {
            orthonormalise(values, cols, rows, 1, cols);     //column j starts at j and steps by cols
        }

        if (scale != 1.0) {
            for (int i = 0; i < values.length; i++) {
                values[i] *= scale;
            }
        }
    }

    /**
     * Modified Gram-Schmidt over {@code count} vectors of {@code length}, addressed through strides so that
     * the same code orthonormalises rows or columns of the flat row-major array.
     *
     * @param outerStride distance between one vector and the next
     * @param innerStride distance between two entries of one vector
     */
    private static void orthonormalise(double[] values, int count, int length, int outerStride, int innerStride) {
        for (int i = 0; i < count; i++) {
            final int start = i * outerStride;

            for (int j = 0; j < i; j++) {                       //subtract as we go, not all at the end
                final int previous = j * outerStride;
                double projection = 0;
                for (int k = 0; k < length; k++) {
                    projection += values[start + k * innerStride] * values[previous + k * innerStride];
                }
                for (int k = 0; k < length; k++) {
                    values[start + k * innerStride] -= projection * values[previous + k * innerStride];
                }
            }

            double norm = 0;
            for (int k = 0; k < length; k++) {
                final double entry = values[start + k * innerStride];
                norm += entry * entry;
            }
            norm = Math.sqrt(norm);

            if (norm < 1e-12) {     //drawn inside the span of the ones before it, which Gaussian draws make vanishingly unlikely
                LOG.warning("Degenerate vector while orthonormalising, leaving it as drawn");
                continue;
            }
            for (int k = 0; k < length; k++) {
                values[start + k * innerStride] /= norm;
            }
        }
    }
}
