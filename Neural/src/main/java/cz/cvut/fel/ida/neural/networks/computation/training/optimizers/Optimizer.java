package cz.cvut.fel.ida.neural.networks.computation.training.optimizers;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Collection;

public interface Optimizer {

    static Optimizer getFrom(Settings settings, Value learningRate) {
        if (settings.getOptimizer() == Settings.OptimizerSet.SGD) {
            return new SGD(learningRate);
        } else if (settings.getOptimizer() == Settings.OptimizerSet.ADAM) {
            return new Adam(learningRate);
        }
        return new SGD(learningRate);  //default
    }


    default void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater, int iteration) {
//        synchronized (this) { //todo where to synchronize?
        performGradientStep(weightUpdater.updatedWeightsOnly, weightUpdater.weightUpdates, iteration);
//        }
    }

    /**
     * Update of the unique (shared) weights should be synchronized across the backpropagating threads
     * @param updatedWeights
     * @param gradients
     * @param iteration
     */
    void performGradientStep(Collection<Weight> updatedWeights, Value[] gradients, int iteration);

    void restart(Settings settings);
}
