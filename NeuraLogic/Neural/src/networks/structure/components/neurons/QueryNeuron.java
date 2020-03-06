package networks.structure.components.neurons;

import evaluation.values.Value;
import learning.Query;
import networks.computation.iteration.actions.Evaluation;
import networks.computation.training.NeuralModel;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.types.AtomNeurons;
import networks.structure.components.neurons.states.State;
import settings.Settings;

/**
 * Created by gusta on 11.3.17.
 */
public class QueryNeuron extends Query<NeuralNetwork<State.Neural.Structure>, NeuralModel> {

    public AtomNeurons neuron;

    @Deprecated
    public QueryNeuron(String id, int queryCounter, double importance) {
        super(id, queryCounter, importance, null);
    }

    public QueryNeuron(String id, int position, double importance, AtomNeurons targetNeuron, NeuralNetwork<State.Neural.Structure> neuralNetwork) {
        super(id, position, importance, neuralNetwork);
        this.neuron = targetNeuron;
    }

    @Override
    public Value evaluate(Settings settings, NeuralModel neuralModel) {
        Evaluation evaluation = new Evaluation(settings);
        return evaluation.evaluate(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (neuron != null)
            sb.append(neuron.toString());
        if (evidence != null)
            sb.append(" <- " + evidence.toString());
        return sb.toString();
    }
}