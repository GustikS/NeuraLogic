package networks.computation.iteration.actions;

import networks.computation.training.NeuralModel;
import networks.computation.training.evaluation.values.Value;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class Backprop extends NeuronVisitor<Value> {

    List<Weight> allWeights;

    Evaluator evaluator;

    public Backprop(Settings settings, int stateIndex, NeuralModel neuralModel) {
        allWeights = neuralModel.getAllWeights();
    }

    Value activateGradient(Neuron neuron){
        Value activatedGradient = neuron.activation.differentiateAt(neuron.state.getInputSum(evaluator)); //todo add offset!
        return activatedGradient;
    }

    public Value visit(WeightedNeuron neuron) {
        return null;
    }

    @Override
    public Value visit(Neurons neuron) {
        return null;
    }

    @Override
    public Value visit(State state) {
        return null;
    }

    @Override
    public int getIndex() {
        return state_index;
    }


    Value getOutputValue(Evaluator evaluator);

    Value getInputSum(NeuronVisitor evaluator); //todo test consider passing int instead of Evaluator for speedup

    void incrementInputSum(Evaluator evaluator, Value value);

    Value getGradient(Backprop backprop);

    void incrementGradient(Backprop backprop, Value value);


    abstract void setOutputValue(Evaluator evaluator, Value value);

    abstract void setGradient(Backprop evaluation, Value value);
}