package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Inverse extends ElementWise {
    private static final Logger LOG = Logger.getLogger(Inverse.class.getName());

    public static final DoubleUnaryOperator inverse = in -> 1 / in;

    private static final DoubleUnaryOperator inverseDiff = in -> -1 / (in * in);

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