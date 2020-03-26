package cz.cvut.fel.ida.neural.networks.computation.training.optimizers;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.logging.Logger;

public class SGD implements Optimizer {
    private static final Logger LOG = Logger.getLogger(SGD.class.getName());

    Value learningRate; //check any direct declaration of Value subclasses for DD   - solved by PROTECTED modifier in the specific methods

    public SGD(Value learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater, int iteration) {
        performGradientStep(neuralModel.weights, weightUpdater.weightUpdates, iteration);
    }

    public void performGradientStep(List<Weight> weights, Value[] weightUpdates, int iteration) {
        for (Weight weight : weights) {
            if (!weight.isLearnable) {
                //fixed weights and constants
            } else {  //todo now speedup - update only those weights that have been changed? (i.e. no zero increment calls - i.e. hold list of updates somewhere?)

                Value update = weightUpdates[weight.index].times(learningRate); //and it's clearer to do it here for each parameter separately

                weight.value.incrementBy(update);
                LOG.finest(() -> "Incrementing " + weight + " with " + update);   //todo check all these string concatenating messages and change them to lambdas for performance, it's significant!
            }
        }
    }

    @Override
    public void restart(Settings settings) {
        //pass
    }
}
