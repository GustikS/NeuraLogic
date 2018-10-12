package networks.evaluation;

import settings.Settings;

import java.util.logging.Logger;

/**
 * todo use in case of input overmapping and parallel evaluation, otherwise just use stateful neurons
 */
@Deprecated
public class EvaluatorWithNeuronStates<S> extends Evaluator {
    private static final Logger LOG = Logger.getLogger(EvaluatorWithNeuronStates.class.getName());

    private final int[] sortedNeuronIndices;
    S[] states;

    public EvaluatorWithNeuronStates(Settings settings, int[] sortedNeuronIndices) {
        super(settings);
        this.sortedNeuronIndices = sortedNeuronIndices;
    }
}