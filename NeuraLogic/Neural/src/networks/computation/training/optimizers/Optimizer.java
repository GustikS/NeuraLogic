package networks.computation.training.optimizers;

import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;

import java.util.List;

public interface Optimizer {

    List<Weight> gradientStep(List<Weight> weights);

    List<Weight> gradientStep(List<Weight> weights, List<Value> weightUpdates);
}
