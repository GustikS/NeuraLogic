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

    private boolean[] active;

    public WeightUpdater(List<Weight> weights) {
        weightUpdates = new Value[weights.size()];
        active = new boolean[weights.size()];

        for (Weight weight : weights) {
            int index = weight.index;
            if (index > weightUpdates.length) {
                LOG.severe("Weight index exceeding number of all extracted weights");
            }
            if (weight.isLearnable()) {
                weightUpdates[index] = weight.value.getForm();
            } else {
                continue;
                //void, these are constant weights not to be updated
                //or these are weights with fixed values by user
            }
            if (active[index]) {
                LOG.severe("Weight index seen twice! Input weight list is not unique! Some weight will try to be updated twice!");
            }
            active[index] = true;
        }
    }

    @Override
    public void visit(Weight weight, Value value) {
        if (weight.isLearnable) {   //faster access version
            int index = weight.index;
            weightUpdates[index].incrementBy(value);
        }
    }

    @Deprecated
    public void visit(StatefulWeight weight, Value value) {
        weight.getAccumulatedUpdate().incrementBy(value); //todo this will probably never get called, is StatefulWeight necessary?
    }

    public void clearUpdates() {
        for (int i = 0; i < weightUpdates.length; i++) {
            Value weightUpdate = weightUpdates[i];
            if (weightUpdate != null)
                weightUpdate.zero();
        }
    }
}
