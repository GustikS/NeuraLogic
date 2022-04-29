package cz.cvut.fel.ida.algebra.functions.specific;

import cz.cvut.fel.ida.algebra.functions.Activation;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Inverse extends Activation {
    private static final Logger LOG = Logger.getLogger(Inverse.class.getName());

    @Override
    public String getName() {
        return Inverse.class.getSimpleName();
    }

    public static final Function<Double, Double> inverse = in -> 1 / in;

    private static final Function<Double, Double> inverseDiff = in -> -1 / (in * in);

    public Inverse() {
        super(inverse, inverseDiff);
    }

    @Override
    public Inverse replaceWithSingleton() {
        return Singletons.inverse;
    }


    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(-5.0, 5.0);
    }

}