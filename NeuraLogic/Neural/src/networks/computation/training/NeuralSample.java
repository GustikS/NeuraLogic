package networks.computation.training;

import evaluation.values.Value;
import learning.LearningSample;
import networks.structure.building.Cachable;
import networks.structure.components.neurons.QueryNeuron;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralSample extends LearningSample<QueryNeuron, Cachable> {

    public NeuralSample(Value v, QueryNeuron q) {
        this.query = q;
        this.target = v;
    }
}

