package networks.structure.neurons;

import networks.evaluation.values.Value;

import java.util.logging.Logger;

/**
 * The most lightweight representation of a Neuron.
 */
@Deprecated
public class IndexedNeuron implements Neurons {
    private static final Logger LOG = Logger.getLogger(IndexedNeuron.class.getName());

    public int id;

    IndexedNeuron[] inputs;
    int[] weightIndices;
    int offsetIndex;

    @Override
    public Value evaluate() {
        return null;
    }

    @Override
    public Value gradient() {
        return null;
    }
}