package networks.computation.evaluation.functions.specific;

import networks.computation.evaluation.functions.Activation;
import networks.structure.metadata.states.AggregationState;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A type of Sigmoidal activation function approximating Lukasiewicz more tightly (steeper gradient and offset -0.5)
 */
public class LukasiewiczSigmoid extends Activation {
    private static final Logger LOG = Logger.getLogger(LukasiewiczSigmoid.class.getName());

    static Function<Double, Double> logist = in -> in > 100 ? 1 : (in < -100 ? 0 : 1 / (1 + Math.exp(-6 * in - 0.5)));

    public LukasiewiczSigmoid() {
        super(logist,
                in -> in > 100 || in < -100 ? 0 : logist.apply(in) * (1 - logist.apply(in))
        );
    }

    @Override
    public AggregationState getAggregationState() {
        return new AggregationState.ActivationState(this);
    }

}
