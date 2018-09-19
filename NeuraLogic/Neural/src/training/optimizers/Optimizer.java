package training.optimizers;

import networks.structure.Weight;

import java.util.List;

public interface Optimizer {

    List<Weight> gradientStep(List<Weight> weights);
}
