package networks.computation.evaluation.functions.specific;

import networks.computation.evaluation.functions.Activation;
import networks.structure.metadata.states.AggregationState;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Sigmoid extends Activation {
    private static final Logger LOG = Logger.getLogger(Sigmoid.class.getName());

    private static final Function<Double, Double> logist = in -> in > 100 ? 1 : (in < -100 ? 0 : 1 / (1 + Math.exp(-in)));

    private static final Function<Double, Double> diffLogist = in -> {
        if (in > 100 || in < -100)
            return 0.0;
        double sigm = 1.0 / (1.0 + Math.exp(-in));  //need to store this local variable here for speedup
        return sigm * (1.0 - sigm);
    };

    public Sigmoid() {
        super(logist, diffLogist);
    }

    @Override
    public Sigmoid replaceWithSingleton() {
        return Singletons.sigmoid;
    }

    @Override
    public AggregationState getAggregationState() {
        return new AggregationState.ActivationState(this);
    }
}