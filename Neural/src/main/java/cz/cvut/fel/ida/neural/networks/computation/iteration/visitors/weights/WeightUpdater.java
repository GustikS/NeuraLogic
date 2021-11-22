package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class WeightUpdater implements WeightVisitor {
    private static final Logger LOG = Logger.getLogger(WeightUpdater.class.getName());

    /**
     * To be used instead of storing the gradient update in the weight object (StatefulWeight) for PARALLEL backproping.
     * Since the number of all unique weights is typically low, each thread has its own full index of weightUpdates.
     * <p>
     * UNSYCHRONIZED storage of weight updates.
     */
    public Value[] weightUpdates;

    /**
     * Stores only the subset of the weight that have been updated during the last iteration (gets cleared in clearUpdates())
     */
    public List<Weight> updatedWeightsOnly;

    public WeightUpdater(List<Weight> learnableWeights, int maxWeightIndex) {

        check4mistakes(learnableWeights, maxWeightIndex);

        weightUpdates = new Value[maxWeightIndex + 1];
        updatedWeightsOnly = new ArrayList<>(maxWeightIndex + 1);
    }

    private void check4mistakes(List<Weight> learnableWeights, int maxWeightIndex) {
        if (maxWeightIndex < learnableWeights.size() - 1) {
            LOG.severe("Weight indices are off (there are more learnable weight than all weights?)!!");
        }

        boolean[] duplicate = new boolean[maxWeightIndex + 1];

        for (Weight weight : learnableWeights) {
            int index = weight.index;
            if (index > maxWeightIndex) {
                LOG.severe("Weight index exceeding number of all extracted allWeights!");
            }
            if (weight.isLearnable) {
//                weightUpdates[index] = weight.value.getForm();    //not necessary anymore
            } else {
                LOG.severe("Fixed weights leaking through into WeightUpdater!! (should have been filtered before)");
            }
            if (duplicate[index]) {
                LOG.severe("Weight index seen twice! Input weight list is not unique! Some weight will try to be updated twice!");
            }
            duplicate[index] = true;
        }
    }

    @Override
    public void visit(Weight weight, Value value) {
        if (weight.isLearnable) {   //faster access version
            int index = weight.index;

            Value weightUpdate = weightUpdates[index];
            if (weightUpdate != null) {
                weightUpdate.incrementBy(value);
            } else {
                weightUpdates[index] = value.clone(); //!! necessary not to share weights in a very weird way!!
                updatedWeightsOnly.add(weight);
            }
        }
    }

    public void clearUpdates() {
        Arrays.fill(weightUpdates, null);
        updatedWeightsOnly.clear();
    }
}
