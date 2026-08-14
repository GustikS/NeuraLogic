package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
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
     * Each thread has its own full index of weightUpdates.
     * <p>
     * UNSYCHRONIZED storage of weight updates.
     * <p>
     * Addressed by {@link Weight#index}, never enumerated as "the weights": the length is capacity, not a weight
     * count, and after growing there are trailing nulls. Ask {@link #updatedWeightsOnly} which weights have an
     * update. It grows because a learnable value on an example fact is created after the model this was sized
     * from, so the old assumption that the number of unique weights is low no longer holds - there is one such
     * weight per constant that wants an embedding.
     * <p>
     * Public rather than encapsulated because the Python frontend reads this field directly over jpype
     * (<code>NeuralogicOptTensor.grad</code>), which sees public fields only. Narrowing it would need the frontend
     * to move at the same time, and a released frontend is routinely run against a newer jar.
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

    /**
     * Scale every accumulated update, which is how a MEAN reduction reaches the gradient: torch divides the
     * loss and lets autograd carry it, and here the same factor is applied to what the optimizer is about to
     * step with. Scaling by one is a no-op and is skipped, so a SUM reduction costs nothing.
     */
    public void scaleUpdates(double factor) {
        if (factor == 1.0) {
            return;
        }
        ScalarValue scale = new ScalarValue(factor);
        for (int i = 0; i < weightUpdates.length; i++) {
            if (weightUpdates[i] != null) {
                weightUpdates[i] = weightUpdates[i].times(scale);   // the idiom Result.weighted already uses
            }
        }
    }

    public void clearUpdates() {
        //Clear whichever is smaller. A slot becomes non-null exactly when its weight enters updatedWeightsOnly,
        //so wiping just those indices is equivalent to wiping the array. Arrays.fill is an intrinsic and stays
        //the cheaper of the two while most of the buffer was touched anyway, which is the ordinary case; the
        //targeted loop is for a buffer holding per-example embeddings a single sample never goes near. Measured
        //crossover is around half the buffer (0.28 ns per element filled against 0.49 ns per element visited).
        if (updatedWeightsOnly.size() < weightUpdates.length / 2) {
            for (int i = 0, size = updatedWeightsOnly.size(); i < size; i++) {
                weightUpdates[updatedWeightsOnly.get(i).index] = null;
            }
        } else {
            Arrays.fill(weightUpdates, null);
        }
        updatedWeightsOnly.clear();
    }
}
