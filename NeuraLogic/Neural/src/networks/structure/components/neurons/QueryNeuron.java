package networks.structure.components.neurons;

import learning.Query;
import networks.computation.training.NeuralModel;
import networks.computation.training.evaluation.values.Value;
import networks.structure.metadata.states.State;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.types.AtomNeuron;

/**
 * Created by gusta on 11.3.17.
 */
public class QueryNeuron extends Query<NeuralNetwork<State.Structure>, NeuralModel> {

    public AtomNeuron neuron;

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