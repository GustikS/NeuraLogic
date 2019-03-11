package networks.computation.iteration.visitors.weights;

import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.StatefulWeight;
import networks.structure.components.weights.Weight;

import java.util.List;
import java.util.logging.Logger;

public class WeightUpdater implements WeightVisitor {
    private static final Logger LOG = Logger.getLogger(WeightUpdater.class.getName());

    /**
     * To be used instead of storing the gradient update in the weight object (StetefulWeight) for PARALLEL backproping.
     * Since the number of all unique weights is typically low, each thread has its own full index of weightUpdates.
     * <p>
     * UNSYCHRONIZED storage of weight updates.
     */
    public Value[] weightUpdates;

    public WeightUpdater(List<Weight> weights) {
        weightUpdates = new Value[weights.size()];

        for (Weight weight : weights) {
            int index = weight.index;
            if (index < 0) {
                //void, these are constant weights not to be updated
            } else if (index > weightUpdates.length) {
                LOG.severe("Weight index exceeding number of all extracted weights");
            } else {
                weightUpdates[index] = weight.value.getForm();
            }
        }
    }

    @Override
    public void visit(Weight weight, Value value) {
        int index = weight.index;
        if (index >= 0)
            weightUpdates[index].incrementBy(value);
    }

    @Deprecated
    public void visit(StatefulWeight weight, Value value) {
        weight.getAccumulatedUpdate().incrementBy(value); //todo this will probably never get called, is StatefulWeight necessary?
    }
}
