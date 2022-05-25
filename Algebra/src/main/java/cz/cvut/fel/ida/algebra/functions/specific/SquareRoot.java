package cz.cvut.fel.ida.algebra.functions.specific;

import cz.cvut.fel.ida.algebra.functions.Activation;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class SquareRoot extends Activation {
    private static final Logger LOG = Logger.getLogger(SquareRoot.class.getName());

    @Override
    public String getName() {
        return SquareRoot.class.getSimpleName();
    }

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