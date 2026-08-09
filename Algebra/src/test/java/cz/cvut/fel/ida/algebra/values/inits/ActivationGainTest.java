package cz.cvut.fel.ida.algebra.values.inits;

import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.Exponentiation;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.ReLu;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.Sigmoid;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.Tanh;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.Identity;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * The numbers are torch's {@code calculate_gain}; what is new here is that nobody has to pass them.
 */
class ActivationGainTest {

    private static final int DRAWS = 20000;

    private static Settings settings() {
        Settings settings = new Settings();
        settings.infer();
        return settings;
    }

    @TestAnnotations.Fast
    public void theNumbersAreTorchsGainTable() {
        assertEquals(5.0 / 3, ActivationGain.of(new Tanh()), 1e-12);
        assertEquals(Math.sqrt(2.0), ActivationGain.of(new ReLu()), 1e-12);
        assertEquals(1.0, ActivationGain.of(new Sigmoid()), 1e-12);
        assertEquals(1.0, ActivationGain.of(new Identity()), 1e-12);
    }

    /**
     * Nothing is corrected for an activation with no known correction, which is what these all did before.
     */
    @TestAnnotations.Fast
    public void anythingWithoutAKnownCorrectionIsLeftAlone() {
        assertEquals(ActivationGain.LINEAR, ActivationGain.of(null), 1e-12);
        assertEquals(ActivationGain.LINEAR, ActivationGain.of(new Exponentiation()), 1e-12);
    }

    private static double widestOf(ValueInitializer initializer, int rows, int cols) {
        MatrixValue matrix = new MatrixValue(rows, cols);
        initializer.initMatrix(matrix);

        double widest = 0;
        for (double value : matrix.values) {
            widest = Math.max(widest, Math.abs(value));
        }
        return widest;
    }

    @TestAnnotations.Fast
    public void aGainWidensTheRangeItIsGivenTo() {
        GlorotUniformInitializer plain = new GlorotUniformInitializer(settings());
        ValueInitializer widened = plain.withGain(5.0 / 3);

        double before = widestOf(plain, DRAWS / 64, 64);
        double after = widestOf(widened, DRAWS / 64, 64);

        assertEquals(5.0 / 3, after / before, 0.02);
    }

    /**
     * A widened initializer is a sibling rather than the same object mutated, so one of them can serve
     * weights of differing gains inside a single pass over the model.
     */
    @TestAnnotations.Fast
    public void wideningHandsBackASeparateInitializer() {
        GlorotUniformInitializer plain = new GlorotUniformInitializer(settings());

        assertNotSame(plain, plain.withGain(5.0 / 3));
        assertSame(plain, plain.withGain(ActivationGain.LINEAR), "asking for the gain it already has");
    }

    /**
     * {@link Settings.InitSet#SIMPLE} names a distribution outright, so correcting it would be answering a
     * question nobody asked - it hands itself back.
     */
    @TestAnnotations.Fast
    public void theDistributionTheUserNamedIsLeftAlone() {
        SimpleInitializer simple = new SimpleInitializer(settings());

        assertSame(simple, simple.withGain(5.0 / 3));
    }

    @TestAnnotations.Fast
    public void aWeightKeepsTheFirstGainItIsGiven() {
        Weight weight = new Weight(0, "w", new MatrixValue(2, 2), false, false);

        weight.setActivationGain(5.0 / 3);
        weight.setActivationGain(Math.sqrt(2.0));

        assertEquals(5.0 / 3, weight.activationGain, 1e-12);
    }
}
