package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class ReLu extends ElementWise {
    private static final Logger LOG = Logger.getLogger(ReLu.class.getName());

    private static final Function<Double, Double> eval = in -> in > 0 ? in : 0.0;

    private static final Function<Double, Double> grad = in -> in > 0 ? 1.0 : 0.0;

    public ReLu() {
        super(eval, grad);
    }

    @Override
    public ReLu replaceWithSingleton() {
        return Singletons.relu;
    }

    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(0.01, Double.MAX_VALUE);
    }

}