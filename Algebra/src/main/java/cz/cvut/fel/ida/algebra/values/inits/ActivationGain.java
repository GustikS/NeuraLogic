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
 * Torch's whole table, against what this library has: {@code tanh} 5/3, {@code relu} sqrt(2),
 * {@code leaky_relu} sqrt(2/(1+slope^2)), {@code selu} 3/4, and {@code linear}, {@code sigmoid} and the
 * convolutions 1. So the only ones missing here are the two with no counterpart - there is no SELU, and no
 * convolution. <b>Sigmoid is deliberately 1</b>, not overlooked: torch lists it with the linear functions.
 * That is arguably generous to it, since a sigmoid's slope at zero is a quarter and preserving variance
 * through one would want considerably more - but the point of these numbers is to be torch's, and departing
 * from them for one activation would make the two engines disagree on exactly the case this exists to line
 * up. Everything else - exp, sqrt, log, inverse, signum, softmax, sparsemax, norm, lukasiewicz - has no
 * entry in that table either, and gets 1 for the same reason.
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
