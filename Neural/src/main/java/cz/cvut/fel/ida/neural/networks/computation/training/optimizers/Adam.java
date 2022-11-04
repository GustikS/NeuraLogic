package cz.cvut.fel.ida.neural.networks.computation.training.optimizers;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Collection;
import java.util.logging.Logger;

public class Adam implements Optimizer {
    private static final Logger LOG = Logger.getLogger(Adam.class.getName());

    public Value learningRate;
    public ScalarValue beta1;
    public ScalarValue beta2;
    public ScalarValue epsilon;

    private ScalarValue minusOne = new ScalarValue(-1);

    public Adam(Value learningRate) {
        this(learningRate, 0.9, 0.999, 1e-8);
    }

    public Adam(Value learningRate, double i_beta1, double i_beta2, double i_epsilon) {
        this.learningRate = learningRate;
        this.beta1 = new ScalarValue(i_beta1);
        this.beta2 = new ScalarValue(i_beta2);
        this.epsilon = new ScalarValue(i_epsilon);
    }

    public void performGradientStep(Collection<Weight> updatedWeights, Value[] gradients, int iteration) {
        //correction
        final ScalarValue fix1 = new ScalarValue(1 / (1 - Math.pow(beta1.value, iteration)));
        final ScalarValue fix2 = new ScalarValue(1 / (1 - Math.pow(beta2.value, iteration)));

        final Value oneBeta1 = Value.ONE.minus(beta1);
        final Value oneBeta2 = Value.ONE.minus(beta2);
        final double eps = this.epsilon.value;

        for (Weight weight : updatedWeights) {
            Value gradient = gradients[weight.index].times(minusOne);    //the gradient

            Value gradientPower = gradient.elementTimes(gradient);
            gradientPower.elementMultiplyBy(oneBeta2);

            weight.velocity.elementMultiplyBy(beta2);
            weight.velocity.incrementBy(gradientPower);

            gradient.elementMultiplyBy(oneBeta1);
            weight.momentum.elementMultiplyBy(beta1);
            weight.momentum.incrementBy(gradient);

            Value v_corr = weight.momentum.times(fix1);
            Value s_corr = weight.velocity.times(fix2);

            //update
            s_corr.applyInplace(val -> (-1 / (Math.sqrt(val) + eps)));
            v_corr.elementMultiplyBy(s_corr);
            v_corr.elementMultiplyBy(learningRate);
            weight.value.incrementBy(v_corr);
        }

    }

    @Override
    public void restart(Settings settings) {

    }
}
