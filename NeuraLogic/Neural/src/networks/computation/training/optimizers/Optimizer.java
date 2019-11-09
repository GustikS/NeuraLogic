package networks.computation.training.optimizers;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.util.List;

public interface Optimizer {

    static Optimizer getFrom(Settings settings, Value learningRate) {
        if (settings.optimizer == Settings.OptimizerSet.SGD) {
            return new SGD(learningRate);
        } else if (settings.optimizer == Settings.OptimizerSet.ADAM) {
            return new Adam(learningRate);
        }
        return new SGD(learningRate);  //default
    }

    void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater);

    void performGradientStep(List<Weight> weights, Value[] weightUpdates);

    void restart(Settings settings);
}
