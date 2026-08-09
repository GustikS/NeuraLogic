package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.LeakyReLu;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.ReLu;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.Tanh;

/**
 * How much wider a weight should be drawn because of the activation its output passes through.
 * <p>
 * These are the numbers in torch's {@code calculate_gain}: a shape-aware initializer keeps the variance of a
 * layer's output equal to its input only for a linear map, and each nonlinearity shrinks it by its own
 * amount, which the gain undoes. Anything without a known correction gets 1, which is what the initializers
 * did for everything before this.
 * <p>
 * Torch makes the caller pass this - {@code kaiming_uniform_(w, nonlinearity="tanh")} - and has to, because
 * when {@code nn.Linear.reset_parameters} runs it genuinely does not know what will be applied to its output
 * later, possibly by an unrelated module. A template says the weight and the activation in the same rule, so
 * here nobody has to be asked. See {@link cz.cvut.fel.ida.logic.constructs.template.Template} for where the
 * two are put together, and note the reason it can be done at all: one <em>lifted</em> rule may ground many
 * times and spread its weight over the network, but every one of those groundings carries the same
 * activation with it.
 */
public class ActivationGain {

    public static final double LINEAR = 1.0;

    /**
     * @param transformation the activation a weight's output passes through, or null where none is stated
     * @return the multiplier for that activation, 1 where there is nothing to correct
     */
    public static double of(Transformation transformation) {
        if (transformation instanceof Tanh) {
            return 5.0 / 3;
        }
        if (transformation instanceof LeakyReLu) {
            return Math.sqrt(2.0 / (1 + LeakyReLu.alpha * LeakyReLu.alpha));
        }
        if (transformation instanceof ReLu) {
            return Math.sqrt(2.0);
        }
        return LINEAR;
    }
}
