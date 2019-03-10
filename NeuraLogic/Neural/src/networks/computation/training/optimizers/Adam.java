package networks.computation.training.optimizers;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.structure.components.weights.Weight;

import java.util.List;
import java.util.logging.Logger;

public class Adam implements Optimizer {
    private static final Logger LOG = Logger.getLogger(Adam.class.getName());

    @Override
    public void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater) {

    }

    @Override
    public void performGradientStep(List<Weight> weights, Value[] weightUpdates) {

    }
}
