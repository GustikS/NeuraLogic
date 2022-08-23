package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Logarithm extends ElementWise {
    private static final Logger LOG = Logger.getLogger(Logarithm.class.getName());

    public static final Function<Double, Double> logist = Math::log;

    private static final Function<Double, Double> diffLogist = in -> 1 / in;

    public Logarithm() {
        super(logist, diffLogist);
    }

    @Override
    public Logarithm replaceWithSingleton() {
        return Singletons.logarithm;
    }


    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(-100.0, 100.0);
    }

}