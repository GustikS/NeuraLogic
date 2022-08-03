package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Identity extends ElementWise {
    private static final Logger LOG = Logger.getLogger(Identity.class.getName());

    private static final Function<Double, Double> identity = in -> in;

    private static final Function<Double, Double> constant = in -> 1.0;

    public Identity() {
        super(identity, constant);
    }

    @Override
    public Identity replaceWithSingleton() {
        return Singletons.identity;
    }
}