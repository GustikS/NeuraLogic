package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Signum extends ElementWise {
    private static final Logger LOG = Logger.getLogger(Signum.class.getName());

    private static final DoubleUnaryOperator signum = in -> in > 0 ? 1.0 : 0.0;

    private static final DoubleUnaryOperator zerograd = in -> 0.0;

    public Signum() {
        super(signum, zerograd);
    }

    @Override
    public Signum replaceWithSingleton() {
        return Singletons.signum;
    }

}