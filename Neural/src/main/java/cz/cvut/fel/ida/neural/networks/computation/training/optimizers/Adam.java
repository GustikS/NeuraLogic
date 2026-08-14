package cz.cvut.fel.ida.neural.networks.computation.training.optimizers;

import cz.cvut.fel.ida.algebra.values.*;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Collection;
import java.util.logging.Logger;

public class Adam implements Optimizer {
    private static final Logger LOG = Logger.getLogger(Adam.class.getName());

    public ScalarValue learningRate;
    public final double beta1;
    public final double beta2;
    public final double epsilon;

    /**
     * Coupled L2, torch's <code>Adam(weight_decay=)</code> - the penalty joins the gradient *before* the
     * moments, so it accumulates in them. That is what makes it different from <code>AdamW</code>, whose
     * decay is applied to the weight directly and bypasses the adaptive scaling; **measured** to be two
     * different updates from the same numbers.
     */
    public final double weightDecay;

    public Adam(Value learningRate) {
        this(learningRate, 0.9, 0.999, 1e-8);
    }

    public Adam(Value learningRate, double i_beta1, double i_beta2, double i_epsilon) {
        this(learningRate, i_beta1, i_beta2, i_epsilon, 0.0);
    }

    public Adam(Value learningRate, double i_beta1, double i_beta2, double i_epsilon, double weightDecay) {
        this.learningRate = (ScalarValue) learningRate;
        this.beta1 = i_beta1;
        this.beta2 = i_beta2;
        this.epsilon = i_epsilon;
        this.weightDecay = weightDecay;
    }

    public void performGradientStep(Collection<Weight> updatedWeights, Value[] gradients, int iteration) {
        //correction
        final double fix1 = 1 / (1 - Math.pow(beta1, iteration));
        final double fix2 = 1 / (1 - Math.pow(beta2, iteration));
        final double lr = learningRate.value;

        for (Weight weight : updatedWeights) {
            final double[] value, momentum, velocity, gradient;

            if (weight.momentum == null || weight.velocity == null) {
                //NeuralModel.init4Adam only reaches the model's own weights - a learnable value on an example fact is
                //created after the model and arrives here without moments
                weight.momentum = weight.value.getForm();
                weight.velocity = weight.value.getForm();
            }

            value = weight.value.getAsArray();
            momentum = weight.momentum.getAsArray();
            velocity = weight.velocity.getAsArray();
            gradient = gradients[weight.index].getAsArray();

            for (int i = 0; i < value.length; i++) {
                //the array holds the descent direction, so torch's `grad = grad + weight_decay * param`
                //subtracts here; `value[i]` is still the weight before this step, which is the one torch reads
                final double grad = weightDecay == 0 ? gradient[i] : gradient[i] - (weightDecay * value[i]);

                momentum[i] = (momentum[i] * beta1) - (grad * (1 - beta1));
                velocity[i] = (velocity[i] * beta2) + (grad * grad * (1 - beta2));
                value[i] += momentum[i] * fix1 * (-1 / (Math.sqrt(velocity[i] * fix2) + epsilon)) * lr;
            }

            weight.value.setAsArray(value);
            weight.momentum.setAsArray(momentum);
            weight.velocity.setAsArray(velocity);
        }
    }

    @Override
    public void restart(Settings settings) {

    }
}