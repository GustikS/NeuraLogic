package cz.cvut.fel.ida.neural.networks.computation.training.optimizers;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
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

    @Override
    public void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater, int iteration) {
        performGradientStep(neuralModel.weights, weightUpdater.weightUpdates, iteration);
    }

    @Override
    public void performGradientStep(List<Weight> weights, Value[] weightUpdates, int iteration) {
        //correction
        ScalarValue fix1 = new ScalarValue(1 / (1 - Math.pow(beta1.value, iteration)));
        ScalarValue fix2 = new ScalarValue(1 / (1 - Math.pow(beta2.value, iteration)));

        for (Weight weight : weights) {
            if (weight.isFixed || weight.index < 0) {
                //fixed weights and constants
            } else {
                Value gradient = weightUpdates[weight.index].times(minusOne);    //the gradient
                weight.velocity = (beta2.times(weight.velocity)).plus(Value.ONE.minus(beta2).times(gradient.elementTimes(gradient)));
                weight.momentum = (beta1.times(weight.momentum)).plus((Value.ONE.minus(beta1)).times(gradient));

                Value v_corr = weight.momentum.times(fix1);
                Value s_corr = weight.velocity.times(fix2);

                //update
                Value divider = s_corr.apply(Math::sqrt).plus(epsilon).apply(val -> (-1 / val));
                Value update = v_corr.elementTimes(divider).times(learningRate);
                weight.value.incrementBy(update);
            }
        }
    }

    @Override
    public void restart(Settings settings) {

    }
}
