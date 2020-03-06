package networks.structure.building;

import evaluation.values.Value;
import networks.computation.training.NeuralSample;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.types.DetailedNetwork;

import java.util.logging.Logger;

public class NeuralProcessingSample extends NeuralSample {
    private static final Logger LOG = Logger.getLogger(NeuralProcessingSample.class.getName());

    public DetailedNetwork detailedNetwork;

    public NeuralProcessingSample(Value v, QueryNeuron q) {
        super(v, q);
        try {
            detailedNetwork = (DetailedNetwork) q.evidence;     //todo remove this class cast by refactoring inside grounder
        } catch (ClassCastException e) {
            LOG.severe("Inappropriate use of NeuralProcessingSample. Use Grounder to create a DetailedNetwork based sample.");
        }
    }
}
