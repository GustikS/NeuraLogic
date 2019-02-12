package networks.computation.iteration.visitors.states.neurons;

import networks.computation.evaluation.values.Value;

import java.util.logging.Logger;

/**
 * To be used instead of storing the gradient update in the weight object (StetefulWeight) for PARALLEL backproping.
 * Since the number of all unique weights is typically low, each backprop has its own full index of weightUpdates.
 */
@Deprecated
public class BackproperWithWeightUpdates extends Backproper {
    private static final Logger LOG = Logger.getLogger(BackproperWithWeightUpdates.class.getName());

    Value[] weightUpdates;

    public BackproperWithWeightUpdates(int stateIndex) {
        super(stateIndex);
    }
}