package cz.cvut.fel.ida.neural.networks.computation.training.optimizers;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Collection;
import java.util.logging.Logger;

public class SGD implements Optimizer {
    private static final Logger LOG = Logger.getLogger(SGD.class.getName());

    Value learningRate; //check any direct declaration of Value subclasses for DD   - solved by PROTECTED modifier in the specific methods

    /**
     * Coupled L2, torch's <code>weight_decay</code>. Zero is off and costs nothing.
     */
    private final double weightDecay;

    /**
     * Hoisted out of the loop - it is the same scalar for every weight and every step.
     */
    private final ScalarValue decayScale;

    public SGD(Value learningRate) {
        this(learningRate, 0.0);
    }

    public SGD(Value learningRate, double weightDecay) {
        this.learningRate = learningRate;
        this.weightDecay = weightDecay;
        this.decayScale = new ScalarValue(weightDecay);
    }

    @Override
    public void performGradientStep(Collection<Weight> updatedWeights, Value[] gradients, int iteration) {
        for (Weight updatedWeight : updatedWeights) {
            Value gradient = gradients[updatedWeight.index];
            if (weightDecay != 0) {
                //torch's `d_p = d_p.add(param, alpha=weight_decay)`, subtracted rather than added because this
                //array holds the descent direction. A new Value, not an increment - torch leaves `.grad` as
                //backward left it, and the frontend reads this array back as the gradient.
                gradient = gradient.minus(updatedWeight.value.times(decayScale));
            }
            updatedWeight.value.incrementBy(gradient.times(learningRate));
        }
    }


    @Override
    public void restart(Settings settings) {
        //pass
    }
}
