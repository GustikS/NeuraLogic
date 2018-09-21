package training;

import networks.evaluation.iteration.State;
import networks.structure.weights.Weight;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * To be used for any PARALLEL backproping with neuron SHARING.
 * There the states of neurons cannot be stored directly by the shared neurons.
 */
public class BackpropWithNeuronStates<S extends State> extends BackpropWithWeightUpdates {
    private static final Logger LOG = Logger.getLogger(BackpropWithNeuronStates.class.getName());

    private final int[] sortedNeuronIndices;
    S[] states;

    public BackpropWithNeuronStates(ArrayList<Weight> weights, int[] sortedNeuronIndices) {
        super(weights);
        this.sortedNeuronIndices = sortedNeuronIndices;
    }
}