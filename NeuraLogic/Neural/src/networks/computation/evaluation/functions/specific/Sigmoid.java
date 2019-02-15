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

    static Function<Double, Double> logist = in -> in > 100 ? 1 : (in < -100 ? 0 : 1 / (1 + Math.exp(-in)));

    public Sigmoid() {
        super(logist,
                in -> in > 100 || in < -100 ? 0 : logist.apply(in) * (1 - logist.apply(in))
        );
    }

    @Override
    public AggregationState getAggregationState() {
        return new AggregationState.ActivationState(this);
    }
}