package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Backpropagation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.Iterator;

/**
 * Down = propagating some values from actual neuron's State to its inputs - e.g. for ({@link Backpropagation}
 * <p>
 * These visitors DO NOT support input masking (i.e. max pooling).
 */
public class StandardDown extends NeuronVisitor.Weighted {

    public StandardDown(NeuralNetwork<State.Neural.Structure> network, StateVisiting.Computation topDown, WeightUpdater weightUpdater) {
        super(network, topDown, weightUpdater);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value gradient = stateVisitor.visit(state);

        Iterator<T> inputs = network.getInputs(neuron);

        T input;
        while (inputs.hasNext()) {
            input = inputs.next();
            input.getComputationView(stateVisitor.stateIndex).storeGradient(gradient);
        }
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value gradient = stateVisitor.visit(state);
        //state.invalidate(); //todo (b) test if faster with invalidation here (at the end of backprop) instead of using separate iteration with networks.computation.iteration.visitors.states.Invalidator ?
        Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);

        weightUpdater.visit(neuron.offset, gradient);

        Iterator<T> inputNeurons = inputs.r;
        Iterator<Weight> inputWeights = inputs.s;
        T input;
        Weight weight;

//        Value transpGradient = gradient.transposedView();    //todo next speedup everything around here, minimize Value copying

        while (inputNeurons.hasNext()) {    //neurons and weights should always be aligned correctly, so skipping the check here for some speedup
            input = inputNeurons.next();
            weight = inputWeights.next();
            State.Neural.Computation inputComputationView = input.getComputationView(stateVisitor.stateIndex);

            Value transpInputValue = inputComputationView.getValue().transposedView();
            weightUpdater.visit(weight, gradient.times(transpInputValue));

//            inputComputationView.storeGradient(transpGradient.times(weight.value));
            inputComputationView.storeGradient(weight.value.transposedView().times(gradient));
        }
    }
}
