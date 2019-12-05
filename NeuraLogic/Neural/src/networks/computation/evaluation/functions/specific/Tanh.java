package networks.computation.evaluation.functions.specific;

import networks.computation.evaluation.functions.Activation;
import networks.structure.metadata.states.AggregationState;
import utils.generic.Pair;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class Tanh extends Activation {
    private static final Logger LOG = Logger.getLogger(Tanh.class.getName());

    private static final Function<Double, Double> tanh = Math::tanh;

    private static final Function<Double, Double> diffTanh = in -> {
        if (in > 100 || in < -100)
            return 0.0;
        double sigm = Math.tanh(in);
        return 1 - (sigm * sigm);
    };

    public Tanh() {
        super(tanh, diffTanh);
    }

    @Override
    public Tanh replaceWithSingleton() {
        return Singletons.tanh;
    }

    @Override
    public AggregationState getAggregationState() {
        return new AggregationState.ActivationState(this);
    }

    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(-0.99, 0.99);
    }
}