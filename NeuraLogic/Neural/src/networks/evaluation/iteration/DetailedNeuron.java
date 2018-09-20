package networks.evaluation.iteration;

import networks.structure.metadata.NeuronMetadata;

import java.util.logging.Logger;

/**
 * This type of a Neuron can be used in a fixed context of a NeuralNetwork.
 * I.e. with no sharing across networks, when it is clear what the parents of this Neuron are.
 */
public class DetailedNeuron extends StatefulNeuron {
    private static final Logger LOG = Logger.getLogger(DetailedNeuron.class.getName());

    int parentCount;
    int parentsChecked;

    NeuronMetadata metadata;

    public DetailedNeuron(String id) {
        super(id);
    }
}