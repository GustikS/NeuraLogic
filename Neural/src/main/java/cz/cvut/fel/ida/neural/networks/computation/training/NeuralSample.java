package cz.cvut.fel.ida.neural.networks.computation.training;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.LearningSample;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralSample extends LearningSample<QueryNeuron, Object> {

    public NeuralSample(Value v, QueryNeuron q, Split type) {
        this.query = q;
        this.target = v;
        this.type = type;
        this.position = q.position;
    }
}

