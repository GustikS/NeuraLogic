package networks.structure.components.neurons;

import networks.computation.evaluation.functions.Activation;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * The most lightweight representation of a Neuron. Not needed anymore since with explicit weight sharing through Weight object, these indices are just pointers to the Weight objects.
 */
@Deprecated
public class IndexedNeuron implements Neurons {
    private static final Logger LOG = Logger.getLogger(IndexedNeuron.class.getName());

    public int id;

    IndexedNeuron[] inputs;
    int[] weightIndices;
    int offsetIndex;

    @Override
    public <T extends Neuron> ArrayList<T> getInputs() {
        return null;
    }

    @Override
    public Activation getActivation() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public int inputCount() {
        return 0;
    }
}