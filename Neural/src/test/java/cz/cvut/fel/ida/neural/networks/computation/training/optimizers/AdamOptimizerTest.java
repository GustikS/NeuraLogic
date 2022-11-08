package cz.cvut.fel.ida.neural.networks.computation.training.optimizers;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdamOptimizerTest {
    private static final Logger LOG = Logger.getLogger(AdamOptimizerTest.class.getName());

    @TestAnnotations.Fast
    public void testStepOnScalars() {
        List<Weight> weights = new ArrayList<>();
        Value[] gradients = new Value[]{
                new ScalarValue(0.4), new ScalarValue(0.4), new ScalarValue(-0.17), new ScalarValue(-0.17),
                new ScalarValue(0.4), new ScalarValue(0.4), new ScalarValue(-0.17), new ScalarValue(-0.17),
        };

        double[] expectedValues = new double[]{
                -0.879000000025,
                0.510999999975,
                -0.5609999999411766,
                -0.2609999999411765,
                -0.8799423078496942,
                0.5100576921503058,
                -0.5600759355766955,
                -0.26007593557669545,
        };

        double[][] weightParams = new double[][]{
                new double[]{-0.88, 0, 0}, new double[]{0.51, 0, 0},
                new double[]{-0.56, 0, 0}, new double[]{-0.26, 0, 0},
                new double[]{-0.88, 0.017, 0.01}, new double[]{0.51, 0.017, 0.01},
                new double[]{-0.56, 0.017, 0.01}, new double[]{-0.26, 0.017, 0.01},
        };

        for (int i = 0; i < expectedValues.length; i++) {
            Weight weight = new Weight(i, "w", new ScalarValue(weightParams[i][0]), false, true);
            weight.momentum = new ScalarValue(weightParams[i][1]);
            weight.velocity = new ScalarValue(weightParams[i][2]);

            weights.add(weight);
        }

        Adam optimizer = new Adam(new ScalarValue(0.001));
        optimizer.performGradientStep(weights.subList(0, 4), gradients, 1);

        for (int i = 0; i < 4; i++) {
            assertEquals(expectedValues[i], ((ScalarValue) weights.get(i).value).value);
        }

        optimizer.performGradientStep(weights.subList(4, 8), gradients, 2);

        for (int i = 4; i < 8; i++) {
            assertEquals(expectedValues[i], ((ScalarValue) weights.get(i).value).value);
        }
    }
}