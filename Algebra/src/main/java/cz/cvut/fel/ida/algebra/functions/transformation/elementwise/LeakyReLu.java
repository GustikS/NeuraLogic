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

    public static double alpha = 0.01;

    @Override
    public String getName() {
        return LeakyReLu.class.getSimpleName();
    }

    private static final DoubleUnaryOperator signum = in -> in > 0 ? in : alpha * in;

    private static final DoubleUnaryOperator zerograd = in -> in > 0 ? 1.0 : alpha;

    public LeakyReLu() {
        super(signum, zerograd);
    }

    @Override
    public LeakyReLu replaceWithSingleton() {
        return Singletons.leakyRelu;
    }

    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(-100.0, 1000.0);
    }

}