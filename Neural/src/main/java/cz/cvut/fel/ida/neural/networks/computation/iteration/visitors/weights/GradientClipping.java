package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

/**
 * Gradient clipping, applied to the accumulated updates between backpropagation and the optimizer step -
 * which is exactly where torch puts it, since <code>clip_grad_norm_</code> is called after
 * <code>backward()</code> and before <code>step()</code>.
 * <p>
 * It operates on the same {@link WeightUpdater#weightUpdates} array the optimizer is about to read, and in
 * place, as torch's do to <code>.grad</code>. That is deliberate: whatever the frontend reads back as the
 * gradient is then the gradient that was actually stepped with.
 * <p>
 * The array holds the *descent direction*, not the gradient - the optimizers increment a weight by it. Both
 * operations here are symmetric under negation (a norm ignores sign, a symmetric clamp commutes with it), so
 * the convention does not enter.
 * <p>
 * Note the weight decay is *not* clipped, again as in torch: it is added inside the optimizer step, after
 * this has run. **Measured** against torch 2.13.
 */
public class GradientClipping {
    private static final Logger LOG = Logger.getLogger(GradientClipping.class.getName());

    /**
     * torch adds this to the norm before dividing, so a gradient clipped to a norm of <code>maxNorm</code>
     * comes out a shade under it rather than exactly at it. Kept because the point is to match, and the
     * difference is measurable: at a norm of 13 clipped to 5, the factor is 0.3846153846 rather than
     * 0.3846153846153846.
     */
    private static final double NORM_EPSILON = 1e-6;

    /**
     * Whatever the settings ask for, in torch's order: the norm first, then the element-wise clamp.
     */
    public static void clip(Value[] gradients, Settings settings) {
        if (settings.gradientClipNorm > 0) {
            clipByNorm(gradients, settings.gradientClipNorm);
        }
        if (settings.gradientClipValue > 0) {
            clipByValue(gradients, settings.gradientClipValue);
        }
    }

    /**
     * torch's <code>clip_grad_norm_</code>: one norm over *all* the gradients together, not per weight, and
     * a single factor applied to all of them. Below the threshold nothing is touched at all - torch clamps
     * the factor at one, and multiplying by one is skipped here rather than done, so an unclipped step is
     * bit-identical to no clipping.
     *
     * @return the total norm before clipping, which is what torch's call returns
     */
    public static double clipByNorm(Value[] gradients, double maxNorm) {
        double sumOfSquares = 0;

        for (Value gradient : gradients) {
            if (gradient == null) {
                continue;
            }
            for (double element : gradient.getAsArray()) {
                sumOfSquares += element * element;
            }
        }

        final double totalNorm = Math.sqrt(sumOfSquares);
        final double factor = maxNorm / (totalNorm + NORM_EPSILON);

        if (factor >= 1.0) {
            return totalNorm;
        }

        ScalarValue scale = new ScalarValue(factor);
        for (int i = 0; i < gradients.length; i++) {
            if (gradients[i] != null) {
                gradients[i] = gradients[i].times(scale);   //the idiom WeightUpdater.scaleUpdates already uses
            }
        }
        return totalNorm;
    }

    /**
     * torch's <code>clip_grad_value_</code>: every element clamped to +-clipValue, each weight on its own.
     * <p>
     * Through {@link Value#applyInplace} rather than the array, because {@link Value#getAsArray} is a view
     * for a vector but a fresh copy for a scalar, so writing through it would silently drop the update on
     * every scalar weight.
     */
    public static void clipByValue(Value[] gradients, double clipValue) {
        final double bound = Math.abs(clipValue);

        for (Value gradient : gradients) {
            if (gradient != null) {
                gradient.applyInplace(element -> Math.max(-bound, Math.min(bound, element)));
            }
        }
    }
}
