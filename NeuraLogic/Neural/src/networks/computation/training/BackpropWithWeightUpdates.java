package networks.computation.training;

import networks.computation.iteration.actions.Backprop;
import networks.computation.training.evaluation.values.Value;
import networks.structure.components.weights.Weight;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * To be used instead of storing the gradient update in the weight object (StetefulWeight) for PARALLEL backproping.
 * Since the number of all unique weights is typically low, each backprop has its own full index of weightUpdates.
 */
public class BackpropWithWeightUpdates extends Backprop {
    private static final Logger LOG = Logger.getLogger(BackpropWithWeightUpdates.class.getName());

    Value[] weightUpdates;

    public BackpropWithWeightUpdates(ArrayList<Weight> weights) {
        super(weights);
    }
}