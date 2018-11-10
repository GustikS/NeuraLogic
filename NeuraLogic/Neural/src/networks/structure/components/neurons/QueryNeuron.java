package networks.structure.components.neurons;

import learning.Query;
import networks.computation.evaluation.Evaluation;
import networks.computation.evaluation.values.Value;
import networks.computation.training.NeuralModel;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.types.AtomNeuron;
import networks.structure.metadata.states.State;
import settings.Settings;

/**
 * Created by gusta on 11.3.17.
 */
public class QueryNeuron extends Query<NeuralNetwork<State.Neural.Structure>, NeuralModel> {

    public AtomNeuron<State.Neural> neuron;

    public QueryNeuron(String id, int queryCounter, double importance) {
        super(id, queryCounter, importance);
    }

    public QueryNeuron(String id, int position, double importance, AtomNeuron<State.Neural> targetNeuron, NeuralNetwork<State.Neural.Structure> neuralNetwork) {
        super(id, position, importance);
        this.neuron = targetNeuron;
        this.evidence = neuralNetwork;
    }

    @Override
    public Value evaluate(Settings settings, NeuralModel neuralModel) {
        Evaluation evaluation = new Evaluation(settings);
        return evaluation.evaluate(this);
    }
}