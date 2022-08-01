package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class ConcatDown extends NeuronVisitor.Weighted {
    private static final Logger LOG = Logger.getLogger(ConcatDown.class.getName());

    public ConcatDown(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor, WeightUpdater weightUpdater) {
        super(network, computationVisitor, weightUpdater);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value gradient = stateVisitor.visit(state);

        Iterator<ScalarValue> elements = getScalarValueIterator(gradient);
        Iterator<T> inputs = network.getInputs(neuron);

        T input;
        while (inputs.hasNext() && elements.hasNext()) {
            input = inputs.next();
            input.getComputationView(stateVisitor.stateIndex).storeGradient(elements.next());
        }
        if (inputs.hasNext() || elements.hasNext()) {
            throw new RuntimeException("Concatenation Gradients dimensions mismatch (only Scalar concatenation can be differentiated now).");
        }
    }


    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
        Value gradient = stateVisitor.visit(state);
        //state.invalidate(); //todo (b) test if faster with invalidation here (at the end of backprop) instead of using separate iteration with networks.computation.iteration.visitors.states.Invalidator ?
        Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);

        weightUpdater.visit(neuron.offset, gradient);

        Iterator<ScalarValue> scalarValueIterator = getScalarValueIterator(gradient);

        Iterator<T> inputNeurons = inputs.r;
        Iterator<Weight> inputWeights = inputs.s;
        T input;
        Weight weight;

        Value transpGradient = gradient.transposedView();    //todo next speedup everything around here, minimize Value copying

        while (inputNeurons.hasNext()) {    //neurons and weights should always be aligned correctly, so skipping the check here for some speedup
            input = inputNeurons.next();
            weight = inputWeights.next();
            ScalarValue elementGradient = scalarValueIterator.next();

            State.Neural.Computation inputComputationView = input.getComputationView(stateVisitor.stateIndex);

            Value transpInputValue = inputComputationView.getValue().transposedView();
            weightUpdater.visit(weight, elementGradient.times(transpInputValue));

//            inputComputationView.storeGradient(elementGradient.times(weight.value));
            inputComputationView.storeGradient(weight.value.transposedView().times(elementGradient));
        }
        if (inputNeurons.hasNext() || scalarValueIterator.hasNext()) {
            throw new RuntimeException("Concatenation Gradients dimensions mismatch (only Scalar concatenation can be differentiated now).");
        }
    }

    private Iterator<ScalarValue> getScalarValueIterator(Value gradient) {
        List<ScalarValue> elementGradients = new ArrayList<>();
        for (Double aDouble : gradient) {
            elementGradients.add(new ScalarValue(aDouble));
        }
        return elementGradients.iterator();
    }
}
