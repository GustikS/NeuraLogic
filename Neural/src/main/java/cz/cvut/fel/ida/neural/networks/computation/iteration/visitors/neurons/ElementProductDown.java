package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.Iterator;
import java.util.logging.Logger;

public class ElementProductDown extends NeuronVisitor.Weighted {
    private static final Logger LOG = Logger.getLogger(ElementProductDown.class.getName());

    public ElementProductDown(NeuralNetwork<State.Structure> network, StateVisiting.Computation stateVisitor, WeightUpdater weightUpdater) {
        super(network, stateVisitor, weightUpdater);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value topGradient = stateVisitor.visit(state);

        Value activationValue = state.getValue().transposedView();

        Iterator<T> inputs = network.getInputs(neuron);

        T input;
        while (inputs.hasNext()) {
            input = inputs.next();

            State.Neural.Computation inputComputationView = input.getComputationView(stateVisitor.stateIndex);

            Value inputValue = inputComputationView.getValue();
            Value grad = activationValue.elementDivideBy(inputValue);
            grad.elementMultiplyBy(topGradient);
            inputComputationView.storeGradient(grad);
        }
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value gradient = stateVisitor.visit(state);
//        Value transpActivationValue = state.getValue().transposedView();

        Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);

        weightUpdater.visit(neuron.offset, gradient);

        Iterator<T> inputNeurons = inputs.r;
        Iterator<Weight> inputWeights = inputs.s;
        T input;
        Weight weight;

        while (inputNeurons.hasNext()) {    //neurons and weights should always be aligned correctly, so skipping the check here for some speedup
            input = inputNeurons.next();
            weight = inputWeights.next();
            State.Neural.Computation inputComputationView = input.getComputationView(stateVisitor.stateIndex);

            Value transpInputValue = inputComputationView.getValue().transposedView();
            Value grad = state.getValue().elementDivideBy(weight.value.times(inputComputationView.getValue()));
            grad.elementMultiplyBy(gradient);

            weightUpdater.visit(weight, grad.times(transpInputValue));

//            inputComputationView.storeGradient(grad.transposedView().times(weight.value));
            inputComputationView.storeGradient(weight.value.transposedView().times(grad));

        }
    }
}