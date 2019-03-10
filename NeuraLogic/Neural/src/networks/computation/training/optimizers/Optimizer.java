package networks.computation.training.optimizers;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.util.List;

public interface Optimizer {

    static Optimizer getFrom(Settings settings) {
        if (settings.optimizer == Settings.OptimizerSet.SGD) {
            return new SGD(settings.initLearningRate);
        } else if (settings.optimizer == Settings.OptimizerSet.ADAM) {
            return new Adam();
        }
        return new SGD(settings.initLearningRate);  //default
    }

    void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater);

    void performGradientStep(List<Weight> weights, Value[] weightUpdates);
}
