package evaluation.functions.specific;

import evaluation.functions.Activation;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Signum extends Activation {
    private static final Logger LOG = Logger.getLogger(Signum.class.getName());

    @Override
    public String getName() {
        return Signum.class.getSimpleName();
    }

    private static final Function<Double, Double> signum = in -> in > 0 ? 1.0 : 0.0;

    private static final Function<Double, Double> zerograd = in -> 0.0;

    public Signum() {
        super(signum, zerograd);
    }

    @Override
    public Signum replaceWithSingleton() {
        return Singletons.signum;
    }

}