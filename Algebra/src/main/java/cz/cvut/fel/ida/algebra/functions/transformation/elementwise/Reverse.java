package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.logging.Logger;

public class Reverse extends ElementWise {
    private static final Logger LOG = Logger.getLogger(Reverse.class.getName());

    @Override
    public String getName() {
        return Reverse.class.getSimpleName();
    }

    public static final DoubleUnaryOperator reverse = in -> (1 - in);

    private static final DoubleUnaryOperator reverseDiff = in -> -1.0;

    public Reverse() {
        super(reverse, reverseDiff);
    }

    @Override
    public Reverse replaceWithSingleton() {
        return Singletons.reverse;
    }


    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(Double.MIN_VALUE, Double.MAX_VALUE);
    }
}
