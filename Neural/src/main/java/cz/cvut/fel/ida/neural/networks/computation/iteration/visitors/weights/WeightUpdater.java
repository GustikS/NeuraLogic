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

    /**
     * Reported once, not once per sample per epoch.
     */
    private boolean negativeIndexReported;

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
            if (weight.isLearnable()) {
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
        if (weight.isLearnable()) {   //faster access version
            int index = weight.index;

            if (index < 0) {
                if (!negativeIndexReported) {
                    negativeIndexReported = true;
                    LOG.severe("Learnable weight with a negative index reached the updater, its gradient is dropped: " + weight);
                }
                return;
            }
            if (index >= weightUpdates.length) {
                //A learnable value on an example fact is created after the model was built, from a weight factory
                //that continues the same index counter - so its index is past the model and there is no slot for it
                //here yet. There is one such weight per constant that wants an embedding, so there can be far more
                //of them than the model has; growing to fit each one in turn would copy the whole array once per
                //weight. Doubling makes the whole run cost a handful of copies instead.
                weightUpdates = Arrays.copyOf(weightUpdates, Math.max(index + 1, weightUpdates.length * 2));
            }

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
        //A slot becomes non-null exactly when its weight is added to updatedWeightsOnly, so these are the only
        //ones to clear. That matters once the array is sized for per-example embeddings rather than for the
        //model alone - it is cleared after every sample, while a single sample touches a handful of weights.
        for (int i = 0, size = updatedWeightsOnly.size(); i < size; i++) {
            weightUpdates[updatedWeightsOnly.get(i).index] = null;
        }
        updatedWeightsOnly.clear();
    }
}
