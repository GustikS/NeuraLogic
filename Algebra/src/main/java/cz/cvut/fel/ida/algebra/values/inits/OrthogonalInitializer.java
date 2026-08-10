package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.distributions.Normal;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

/**
 * A matrix whose rows, or columns where there are fewer of those, are orthonormal - so multiplying by it
 * leaves the length of a vector alone exactly, rather than on average.
 * <p>
 * The distinction only shows up when one weight is applied over and over, which is what a recursive rule
 * does and what no fixed-depth network does. **Measured** on a linear recurrence, width 16, one weight per
 * step, mean magnitude over ten draws: {@link GlorotUniformInitializer} holds its mean across depth - `0.78`
 * at one step and `1.24` at sixteen, against `813829` for the un-scaled default and `0.0002` for
 * {@link TorchUniformInitializer} - but the largest and smallest of those ten draws are `19x` apart by depth
 * sixteen, from `1.7x` at depth one. The mean is not the problem; the spread is, and it is what turns into
 * seed-to-seed variance in training. Every singular value being exactly 1 removes it.
 * <p>
 * Only {@link #initMatrix} differs. Orthogonality is a property of a matrix, and a template's recurrent
 * weight is one; vectors and scalars keep what {@link GlorotUniformInitializer} does with them.
 * <p>
 * Orthonormalisation is modified Gram-Schmidt over Gaussian draws, in place. Classical Gram-Schmidt loses
 * orthogonality to rounding as the dimension grows and the modified form does not, which matters here
 * because the whole point is a property holding exactly.
 */
public class OrthogonalInitializer extends GlorotUniformInitializer {
    private static final Logger LOG = Logger.getLogger(OrthogonalInitializer.class.getName());

    private final Normal gaussian;

    public OrthogonalInitializer(Settings settings) {
        super(settings);
        this.gaussian = new Normal(settings.random, settings);
    }

    @Override
    public ValueInitializer withGain(double gain) {
        if (gain == this.gain) {
            return this;
        }
        OrthogonalInitializer widened = new OrthogonalInitializer(this.settings);
        widened.gain = gain;
        return widened;
    }

    @Override
    public void initMatrix(MatrixValue matrix) {
        final int rows = matrix.rows;
        final int cols = matrix.cols;
        final double[] values = matrix.values;

        for (int i = 0; i < values.length; i++) {
            values[i] = gaussian.getDoubleValue();
        }

        //orthonormalise whichever of the two there are fewer of - with more vectors than dimensions no set of
        //them can be orthogonal, so the shorter side is the one that can carry the property
        if (rows <= cols) {
            orthonormaliseRows(values, rows, cols, cols, 1);    //row i starts at i*cols and steps by one
        } else {
            orthonormaliseRows(values, cols, rows, 1, cols);    //column j starts at j and steps by cols
        }

        if (gain != ActivationGain.LINEAR) {
            for (int i = 0; i < values.length; i++) {
                values[i] *= gain;
            }
        }
    }

    /**
     * Modified Gram-Schmidt over {@code count} vectors of {@code length}, addressed through a stride so that
     * the same code orthonormalises rows or columns of the flat row-major array.
     *
     * @param outerStride distance between one vector and the next
     * @param innerStride distance between two entries of one vector
     */
    private static void orthonormaliseRows(double[] values, int count, int length, int outerStride, int innerStride) {
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
