package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.training.optimizers.Adam;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A learnable value on an example fact is created after the model is built, from a weight factory that continues
 * the same index counter, so its index is past everything the model knows about. It still has to be trainable:
 * the updater has no slot for it yet, and {@link cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel}
 * never gave it Adam moments.
 */
public class LateWeightTest {
    private static final Logger LOG = Logger.getLogger(LateWeightTest.class.getName());

    @TestAnnotations.Fast
    public void updaterKeepsAnUpdateForAWeightPastTheModel() {
        Weight modelWeight = new Weight(0, "w0", new ScalarValue(1.0), false, true);
        WeightUpdater updater = new WeightUpdater(Collections.singletonList(modelWeight), 0);

        Weight lateWeight = new Weight(3, "factValue", new ScalarValue(1.0), false, true);
        updater.visit(lateWeight, new ScalarValue(0.5));

        assertTrue(updater.updatedWeightsOnly.contains(lateWeight), "the late weight has to be reported as updated");
        assertNotNull(updater.weightUpdates[lateWeight.index], "its gradient has to survive");
        assertEquals(0.5, updater.weightUpdates[lateWeight.index].getAsArray()[0], 1e-12);
    }

    @TestAnnotations.Fast
    public void growingIsAmortisedAndClearingLeavesNothingBehind() {
        Weight modelWeight = new Weight(0, "w0", new ScalarValue(1.0), false, true);
        WeightUpdater updater = new WeightUpdater(Collections.singletonList(modelWeight), 0);

        int weightCount = 4096;     //one embedding per constant is the point of learnable facts, so this is a realistic count
        List<Weight> late = new ArrayList<>(weightCount);
        for (int i = 1; i <= weightCount; i++) {
            late.add(new Weight(i, "factValue" + i, new ScalarValue(1.0), false, true));
        }

        for (Weight weight : late) {
            updater.visit(weight, new ScalarValue(0.5));
        }
        assertEquals(weightCount, updater.updatedWeightsOnly.size(), "every weight has to be recorded");
        //Copies cannot be counted from outside, but overshooting the last index is what tells the two strategies
        //apart: growing to fit exactly would end at weightCount + 1 and cost one copy per weight.
        assertTrue(updater.weightUpdates.length > weightCount + 1,
                "the buffer has to grow geometrically, got capacity " + updater.weightUpdates.length
                        + " for " + weightCount + " weights");

        //Most of the buffer was touched, so this goes through Arrays.fill
        updater.clearUpdates();
        assertTrue(updater.updatedWeightsOnly.isEmpty());
        assertBufferEmpty(updater, "clearing a mostly-touched buffer");

        //A handful out of thousands, which is what a sample does once the buffer holds per-example embeddings
        for (Weight weight : late.subList(0, 3)) {
            updater.visit(weight, new ScalarValue(0.5));
        }
        updater.clearUpdates();
        assertTrue(updater.updatedWeightsOnly.isEmpty());
        assertBufferEmpty(updater, "clearing a barely-touched buffer");
    }

    private static void assertBufferEmpty(WeightUpdater updater, String what) {
        for (int i = 0; i < updater.weightUpdates.length; i++) {
            assertEquals(null, updater.weightUpdates[i], what + " has to leave slot " + i + " empty");
        }
    }

    @TestAnnotations.Fast
    public void adamStepsAWeightThatWasNeverGivenMoments() {
        Weight lateWeight = new Weight(0, "factValue", new ScalarValue(1.0), false, true);
        assertEquals(null, lateWeight.momentum, "the premise: init4Adam never reached this weight");

        new Adam(new ScalarValue(0.1))
                .performGradientStep(Collections.singletonList(lateWeight), new ScalarValue[]{new ScalarValue(0.5)}, 1);

        assertNotNull(lateWeight.momentum, "the moments have to be filled in on the way");
        assertTrue(Math.abs(lateWeight.value.getAsArray()[0] - 1.0) > 1e-9,
                "the value has to move rather than throw, got " + lateWeight.value);
    }
}
