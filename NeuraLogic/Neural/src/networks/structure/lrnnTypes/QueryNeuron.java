package networks.structure.lrnnTypes;

import constructs.example.QueryAtom;
import learning.Query;
import networks.evaluation.values.Value;
import networks.structure.NeuralNetwork;
import networks.structure.Neuron;
import training.NeuralModel;

/**
 * Created by gusta on 11.3.17.
 */
public class QueryNeuron extends Query<NeuralNetwork, NeuralModel> {

    Neuron neuron;

    public QueryNeuron(String id, int queryCounter, double importance) {
        super(id, queryCounter, importance);
    }

    public QueryNeuron(QueryAtom queryAtom, NeuralNetwork neuralNetwork) {
        super(queryAtom.ID, queryAtom.position, queryAtom.importance);
        neuron = neuralNetwork.getNeuron(queryAtom.headAtom.literal);
    }

    @Override
    public Value evaluate(NeuralModel neuralNet) {
        return null;
    }

    public Value evaluate() {
        return null;
    }
}