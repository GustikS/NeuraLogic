package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
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
 * These visitors DO NOT support input masking - they just propagate through all the inputs
 * <p>
 * todo now now check offsets and their gradients
 */
public class Down extends NeuronVisitor.Weighted {

    public Down(NeuralNetwork<State.Neural.Structure> network, StateVisiting.Computation topDown, WeightUpdater weightUpdater) {
        super(network, topDown, weightUpdater);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
//        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
//        Value gradient = stateVisitor.visit(state);

        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);

        Value topGradient = state.getGradient();
        ActivationFcn.State fcnState = state.getFcnState();
        fcnState.ingestTopGradient(topGradient);

        Iterator<T> inputs = network.getInputs(neuron);

        T input;
        while (inputs.hasNext()) {
            input = inputs.next();
            Value inputGradient = fcnState.nextInputGradient();
            input.getComputationView(stateVisitor.stateIndex).storeGradient(inputGradient);
        }
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);

        Value topGradient = state.getGradient();
        ActivationFcn.State fcnState = state.getFcnState();
        fcnState.ingestTopGradient(topGradient);

        //state.invalidate(); //todo (b) test if faster with invalidation here (at the end of backprop) instead of using separate iteration with networks.computation.iteration.visitors.states.Invalidator ? Not a good idea, what if we want to reuse the values?
        Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);

        if (neuron.offset.value != Value.ZERO) {
            Value offsetGradient = fcnState.nextInputGradient();    // the offset is always the first Value in the List !
            weightUpdater.visit(neuron.offset, offsetGradient);
        }

        Iterator<T> inputNeurons = inputs.r;
        Iterator<Weight> inputWeights = inputs.s;
        T input;
        Weight weight;

//        Value transpGradient = gradient.transposedView();

        while (inputNeurons.hasNext()) {    //neurons and weights should always be aligned correctly, so skipping the check here for some speedup
            input = inputNeurons.next();
            weight = inputWeights.next();
            State.Neural.Computation inputComputationView = input.getComputationView(stateVisitor.stateIndex);

            Value transpInputValue = inputComputationView.getValue().transposedView();
            Value inputGradient = fcnState.nextInputGradient();
            weightUpdater.visit(weight, inputGradient.times(transpInputValue));

//            inputComputationView.storeGradient(inputGradient.transposedView().times(weight.value));
//            inputComputationView.storeGradient(weight.value.transposedView().times(inputGradient));     //speedup the matrix transposition here with a custom transposedTimes? -> done

            inputComputationView.storeGradient(weight.value.transposedTimes(inputGradient));
        }
    }
}
