package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.AggregationState;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.Iterator;
import java.util.logging.Logger;

public class ProductDown extends NeuronVisitor.Weighted {
    private static final Logger LOG = Logger.getLogger(ProductDown.class.getName());

    public ProductDown(NeuralNetwork<State.Structure> network, StateVisiting.Computation stateVisitor, WeightUpdater weightUpdater) {
        super(network, stateVisitor, weightUpdater);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value gradient = stateVisitor.visit(state);
        AggregationState.ProductState productState = (AggregationState.ProductState) state.getAggregationState();

        Iterator<T> inputs = network.getInputs(neuron);

        T input;
        int i = 0;
        while (inputs.hasNext()) {
            input = inputs.next();

            State.Neural.Computation inputComputationView = input.getComputationView(stateVisitor.stateIndex);
            Value derivative = productState.derivativeFrom(i++, gradient);

            inputComputationView.storeGradient(derivative);
        }
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value gradient = stateVisitor.visit(state);
        AggregationState.ProductState productState = (AggregationState.ProductState) state.getAggregationState();

        Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);

        weightUpdater.visit(neuron.offset, gradient);

        Iterator<T> inputNeurons = inputs.r;
        Iterator<Weight> inputWeights = inputs.s;
        T input;
        Weight weight;

        int i = 0;
        while (inputNeurons.hasNext()) {    //neurons and weights should always be aligned correctly, so skipping the check here for some speedup
            input = inputNeurons.next();
            weight = inputWeights.next();

            State.Neural.Computation inputComputationView = input.getComputationView(stateVisitor.stateIndex);
            Value inputValue = inputComputationView.getValue().transposedView();
            Value derivative = productState.derivativeFrom(i++, gradient);

            weightUpdater.visit(weight, derivative.times(inputValue));

//            inputComputationView.storeGradient(derivative.transposedView().times(weight.value));
            inputComputationView.storeGradient(weight.value.transposedView().times(derivative));
        }
    }
}
