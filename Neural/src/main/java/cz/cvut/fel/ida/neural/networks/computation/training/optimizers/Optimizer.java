package cz.cvut.fel.ida.neural.networks.computation.training.optimizers;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;

public interface Optimizer{

    static Optimizer getFrom(Settings settings, Value learningRate) {
        if (settings.getOptimizer() == Settings.OptimizerSet.SGD) {
            return new SGD(learningRate);
        } else if (settings.getOptimizer() == Settings.OptimizerSet.ADAM) {
            return new Adam(learningRate);
        }
        return new SGD(learningRate);  //default
    }

    void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater, int iteration);

    void performGradientStep(List<Weight> weights, Value[] weightUpdates, int iteration);

    void restart(Settings settings);
}
