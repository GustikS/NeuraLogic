package networks.structure.neurons;

import learning.Query;
import networks.evaluation.values.Value;
import networks.structure.networks.NeuralNetwork;
import networks.structure.neurons.creation.AtomNeuron;
import training.NeuralModel;

/**
 * Created by gusta on 11.3.17.
 */
public class QueryNeuron extends Query<NeuralNetwork, NeuralModel> {

    AtomNeuron neuron;

    public QueryNeuron(String id, int queryCounter, double importance) {
        super(id, queryCounter, importance);
    }

    public QueryNeuron(String id, int position, double importance, AtomNeuron targetNeuron, NeuralNetwork neuralNetwork) {
        super(id, position, importance);
        this.neuron = targetNeuron;
        this.evidence = neuralNetwork;
    }

    @Override
    public Value evaluate(NeuralModel neuralNet) {
        return null;
    }

    public Value evaluate() {
        return null;
    }
}