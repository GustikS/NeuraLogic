package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Exponentiation extends ElementWise {
    private static final Logger LOG = Logger.getLogger(Exponentiation.class.getName());

    public static final DoubleUnaryOperator logist = Math::exp;

    private static final DoubleUnaryOperator diffLogist = Math::exp;

    public Exponentiation() {
        super(logist, diffLogist);
    }

    @Override
    public Exponentiation replaceWithSingleton() {
        return Singletons.exponentiation;
    }


    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(-5.0, Double.MAX_VALUE);
    }

}