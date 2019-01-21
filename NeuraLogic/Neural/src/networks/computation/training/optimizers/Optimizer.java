package networks.computation.training.optimizers;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.actions.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.structure.components.weights.Weight;

import java.util.List;

public interface Optimizer {

    void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater);

    void performGradientStep(List<Weight> weights, Value[] weightUpdates);
}
