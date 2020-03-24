package cz.cvut.fel.ida.neural.networks.structure.components.neurons;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.Query;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Evaluation;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

/**
 * Created by gusta on 11.3.17.
 */
public class QueryNeuron extends Query<NeuralNetwork<State.Neural.Structure>, NeuralModel> implements Exportable {

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