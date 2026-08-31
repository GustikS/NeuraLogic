package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class LeakyReLu extends ElementWise {
    private static final Logger LOG = Logger.getLogger(LeakyReLu.class.getName());

    /**
     * The slope used by every instance that was not given one of its own, and the only slope there used to be.
     * <p>
     * Still a mutable static, and still read at call time by the no-argument form, so setting it goes on
     * affecting those instances exactly as before - including ones built earlier. An instance constructed
     * with an explicit slope ignores it.
     */
    public static double alpha = 0.01;

    /**
     * This instance's own slope, or null for "whatever {@link #alpha} says". Null is not the same as holding
     * the current value of {@link #alpha}: it is what keeps the static's old meaning, that changing it moves
     * the default everywhere rather than only for whatever is built next.
     */
    private final Double explicitSlope;

    @Override
    public String getName() {
        return LeakyReLu.class.getSimpleName();
    }

    private static final DoubleUnaryOperator signum = in -> in > 0 ? in : alpha * in;

    private static final DoubleUnaryOperator zerograd = in -> in > 0 ? 1.0 : alpha;

    public LeakyReLu() {
        super(signum, zerograd);
        this.explicitSlope = null;
    }

    /**
     * A LeakyReLu with a slope of its own, so that two rules in one template can ask for different ones -
     * which the static alone cannot express, being global to the JVM. PyG's GATv2 wants `0.2` where the
     * default here is `0.01`.
     */
    public LeakyReLu(double slope) {
        super(in -> in > 0 ? in : slope * in, in -> in > 0 ? 1.0 : slope);
        this.explicitSlope = slope;
    }

    /**
     * The slope this instance actually applies.
     * <p>
     * Read it rather than {@link #alpha} whenever the answer has to match what the function computes -
     * {@link cz.cvut.fel.ida.algebra.values.inits.ActivationGain} scales an initializer by it, and getting
     * that from the static would size the weights for a slope the neuron does not use.
     */
    public double slope() {
        return explicitSlope == null ? alpha : explicitSlope;
    }

    @Override
    public LeakyReLu replaceWithSingleton() {
        //an instance carrying its own slope is not interchangeable with the shared one
        return explicitSlope == null ? Singletons.leakyRelu : this;
    }

    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(-100.0, 1000.0);
    }

}
