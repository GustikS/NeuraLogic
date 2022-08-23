package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Exponentiation extends ElementWise {
    private static final Logger LOG = Logger.getLogger(Exponentiation.class.getName());

    public static final Function<Double, Double> logist = in ->  Math.exp(in);

    private static final Function<Double, Double> diffLogist = in -> Math.exp(in);

    public Exponentiation() {
        super(logist, diffLogist);
    }

    @Override
    public Exponentiation replaceWithSingleton() {
        return Singletons.exponentiation;
    }


    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(0.01, Double.MAX_VALUE);
    }

}