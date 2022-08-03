package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class SquareRoot extends ElementWise {
    private static final Logger LOG = Logger.getLogger(SquareRoot.class.getName());


    public static final Function<Double, Double> sqrt = in -> Math.sqrt(in);

    private static final Function<Double, Double> diffSqrt = in -> 0.5 * Math.pow(in, -0.5);

    public SquareRoot() {
        super(sqrt, diffSqrt);
    }

    @Override
    public SquareRoot replaceWithSingleton() {
        return Singletons.sqrt;
    }


    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(0.0, 10.0);
    }

}