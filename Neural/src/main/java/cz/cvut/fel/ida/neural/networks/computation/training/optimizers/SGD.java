package cz.cvut.fel.ida.neural.networks.computation.training.optimizers;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Collection;
import java.util.logging.Logger;

public class SGD implements Optimizer {
    private static final Logger LOG = Logger.getLogger(SGD.class.getName());

    Value learningRate; //check any direct declaration of Value subclasses for DD   - solved by PROTECTED modifier in the specific methods

    public SGD(Value learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public void performGradientStep(Collection<Weight> updatedWeights, Value[] gradients, int iteration) {
        for (Weight updatedWeight : updatedWeights) {
            Value weightUpdate = gradients[updatedWeight.index].times(learningRate);
            updatedWeight.value.incrementBy(weightUpdate);
        }
    }


    @Override
    public void restart(Settings settings) {
        //pass
    }
}
