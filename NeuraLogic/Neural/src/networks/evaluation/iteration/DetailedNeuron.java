package networks.evaluation.iteration;

import networks.structure.metadata.NeuronMetadata;

import java.util.logging.Logger;

public class DetailedNeuron extends StatefulNeuron {
    private static final Logger LOG = Logger.getLogger(DetailedNeuron.class.getName());

    int parentCount;
    int parentsChecked;
    
    NeuronMetadata metadata;

    public DetailedNeuron(String id) {
        super(id);
    }
}
