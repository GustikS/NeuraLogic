package training.optimizers;

import networks.evaluation.values.Value;
import networks.structure.weights.Weight;

import java.util.List;

public interface Optimizer {

    List<Weight> gradientStep(List<Weight> weights);

    List<Weight> gradientStep(List<Weight> weights, List<Value> weightUpdates);
}
