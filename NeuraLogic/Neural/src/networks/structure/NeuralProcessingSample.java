package networks.structure;

import networks.computation.values.Value;
import networks.structure.neurons.QueryNeuron;
import networks.structure.networks.types.DetailedNetwork;
import networks.computation.training.NeuralSample;

import java.util.logging.Logger;

public class NeuralProcessingSample extends NeuralSample {
    private static final Logger LOG = Logger.getLogger(NeuralProcessingSample.class.getName());

    DetailedNetwork detailedNetwork;

    public NeuralProcessingSample(Value v, QueryNeuron q) {
        super(v, q);
        try {
            detailedNetwork = (DetailedNetwork) q.evidence;     //todo remove this class cast by refactoring inside grounder
        } catch (ClassCastException e) {
            LOG.severe("Inappropriate use of NeuralProcessingSample. Use Grounder to create a DetailedNetwork based sample.");
        }
    }
}
