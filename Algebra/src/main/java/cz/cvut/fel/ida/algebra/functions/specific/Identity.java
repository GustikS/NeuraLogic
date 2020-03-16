package cz.cvut.fel.ida.algebra.functions.specific;

import cz.cvut.fel.ida.algebra.functions.Activation;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Identity extends Activation {
    private static final Logger LOG = Logger.getLogger(Identity.class.getName());

    @Override
    public String getName() {
        return Identity.class.getSimpleName();
    }

    private static final Function<Double, Double> signum = in -> in;

    private static final Function<Double, Double> zerograd = in -> 1.0;

    public Identity() {
        super(signum, zerograd);
    }

    @Override
    public Identity replaceWithSingleton() {
        return Singletons.identity;
    }
}