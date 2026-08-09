package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.algebra.values.distributions.Uniform;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

/**
 * Uniform on {@code (-1/sqrt(fan_in), 1/sqrt(fan_in))}, which is what torch draws for every layer it ships.
 * <p>
 * {@code torch.nn.Linear} initialises its weight with {@code kaiming_uniform_(a=sqrt(5))}, whose bound is
 * {@code sqrt(6 / ((1 + a^2) * fan_in))} and therefore exactly {@code 1/sqrt(fan_in)};
 * {@code torch.nn.RNN}, {@code LSTM} and {@code GRU} draw every weight from
 * {@code U(+-1/sqrt(hidden_size))}, which is the same thing for their square recurrent weight. The point of
 * matching it is that the two engines given the same model then also start from the same place - the
 * arithmetic between them was already checked, the starting point was not, and that difference is enough to
 * make one converge somewhere the other does not.
 * <p>
 * What this fixes about {@link SimpleInitializer}, the previous default: it draws from one range whatever
 * the weight's dimensions, so a {@code 1 x d} readout sums d terms of the same size and its output grows
 * with d. Under a sigmoid a wide template then starts saturated and spends its first updates undoing that.
 * <p>
 * A vector keeps only one of its two declared dimensions, and its orientation says which, so a row vector
 * consumes its whole length and a column vector consumes one. Narrowing both of them instead, on the
 * argument that the narrower guess is the safer one, is measurably wrong - it also narrows the {@code (n,1)}
 * weights that torch draws from the full {@code (-1, 1)}, and that alone stopped
 * {@code test_xor_generalization} converging in 5000 epochs where it had converged before. Only the weights
 * whose fan-in is both unambiguous and large change at all, which are the ones the saturation was about.
 */
public class TorchUniformInitializer implements ValueInitializer {
    private static final Logger LOG = Logger.getLogger(TorchUniformInitializer.class.getName());

    Uniform distribution;

    public TorchUniformInitializer(Settings settings) {
        this.distribution = new Uniform(settings.random, settings);
    }

    @Override
    public void initScalar(ScalarValue scalar) {
        double limit = getLimit(scalar);
        scalar.value = distribution.getDoubleValue(-limit, limit);
    }

    @Override
    public void initVector(VectorValue vector) {
        double limit = getLimit(vector);
        for (int i = 0; i < vector.values.length; i++) {
            vector.values[i] = distribution.getDoubleValue(-limit, limit);
        }
    }

    @Override
    public void initMatrix(MatrixValue matrix) {
        final double limit = getLimit(matrix);
        final double[] values = matrix.values;

        for (int i = 0; i < values.length; i++) {
            values[i] = distribution.getDoubleValue(-limit, limit);
        }
    }

    /**
     * The weight multiplies from the left, so its columns are the inputs it consumes.
     */
    protected double getLimit(MatrixValue value) {
        return 1.0 / Math.sqrt(value.cols);
    }

    /**
     * Orientation is what says which of the two dimensions was dropped. A row vector was declared
     * {@code (1,n)} and consumes n inputs, so it is the {@code torch.nn.Linear(n, 1)} case; a column vector
     * was declared {@code (n,1)} or as a plain {@code (n)}, consumes one, and is
     * {@code torch.nn.Linear(1, n)}, which torch draws from the full {@code (-1, 1)}.
     */
    protected double getLimit(VectorValue value) {
        return value.rowOrientation ? 1.0 / Math.sqrt(value.values.length) : 1.0;
    }

    /**
     * One input, so the bound is one - the same {@code torch.nn.Linear(1, 1)} gets.
     */
    protected double getLimit(ScalarValue value) {
        return 1.0;
    }
}
