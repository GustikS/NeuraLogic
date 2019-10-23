package networks.computation.training.optimizers;

import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.util.List;
import java.util.logging.Logger;

public class Adam implements Optimizer {
    private static final Logger LOG = Logger.getLogger(Adam.class.getName());

    public ScalarValue learningRate;
    public ScalarValue beta1;
    public ScalarValue beta2;
    public ScalarValue epsilon;

    private long iterrationCount = 1;   //todo move this as parameter into performGradientStep

    private ScalarValue minusOne = new ScalarValue(-1);

    public Adam(double i_alpha) {
        this(i_alpha, 0.9, 0.999, 1e-8);
    }

    public Adam(double i_alpha, double i_beta1, double i_beta2, double i_epsilon) {
        this.learningRate = new ScalarValue(i_alpha);
        this.beta1 = new ScalarValue(i_beta1);
        this.beta2 = new ScalarValue(i_beta2);
        this.epsilon = new ScalarValue(i_epsilon);
    }

    @Override
    public void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater) {
        performGradientStep(neuralModel.weights, weightUpdater.weightUpdates);
    }

    @Override
    public void performGradientStep(List<Weight> weights, Value[] weightUpdates) {
        //correction
        ScalarValue fix1 = new ScalarValue(1 / (1 - Math.pow(beta1.value, this.iterrationCount)));
        ScalarValue fix2 = new ScalarValue(1 / (1 - Math.pow(beta2.value, this.iterrationCount)));

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
                Value update = v_corr.elementTimes(divider);
                weight.value.incrementBy(update.times(learningRate));
            }
        }
        this.iterrationCount++;
    }

    @Override
    public void restart(Settings settings) {
        iterrationCount = 1;
    }
}
