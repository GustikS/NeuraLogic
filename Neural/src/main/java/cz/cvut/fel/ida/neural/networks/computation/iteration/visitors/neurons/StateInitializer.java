package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
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

import static cz.cvut.fel.ida.algebra.weights.Weight.unitWeight;

/**
 * Initializer = loading input values from inputs of a neuron into its State, initializing the State and evaluating
 */
public class StateInitializer extends NeuronVisitor.Weighted {
    private static final Logger LOG = Logger.getLogger(StateInitializer.class.getName());

    public StateInitializer(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor) {
        super(network, computationVisitor, null);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
        State.Neural.Computation computationState = neuron.getComputationView(stateVisitor.stateIndex);
        Iterator<T> inputs = network.getInputs(neuron);
        T input;
        List<Value> inputValues = new ArrayList<>();

        while (inputs.hasNext()) {
            input = inputs.next();
            Value inputValue = input.getComputationView(stateVisitor.stateIndex).getValue();
            if (inputValue == null) {
                LOG.severe("Input Value missing for input neuron: " + input.getName() + " of output neuron: " + neuron +
                        "The input neuron should have been processed before the output neuron - aren't there cycles in the computation graph?");
//                throw new Exception("Input Value missing for input neuron: " + input.getName() + " of output neuron: " + neuron +
//                        ". The input neuron was not processed before the output - aren't there cycles in the computation graph?");
            }
            inputValues.add(inputValue);
        }

        // check first if the state can be made more efficient
        ActivationFcn.State updatedState = ActivationFcn.State.getState(computationState.getFcnState(), inputValues);
        computationState.setFcnState(updatedState);

        // initialize state values and evaluate
        computationState.initEval(inputValues);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        State.Neural.Computation computationState = neuron.getComputationView(stateVisitor.stateIndex);
        Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);
        Iterator<T> inputNeurons = inputs.r;
        Iterator<Weight> inputWeights = inputs.s;
        T input;
        Weight weight;
        List<Value> inputValues = new ArrayList<>();

        if (neuron.offset.value != Value.ZERO)  // only add input if it is not void - otherwise there are problems with initialization of the Value form
            inputValues.add(neuron.offset.value);   // the offset is always the first Value in the List !

        while (inputNeurons.hasNext()) {
            input = inputNeurons.next();
            weight = inputWeights.next();
            if (weight == null) {
                LOG.severe("Weight for input missing, deducing unit weight value for: " + input.getName());
                weight = unitWeight;
            }
            Value inputValue = input.getComputationView(stateVisitor.stateIndex).getValue();
            if (inputValue == null) {
                LOG.severe("Input Value missing for input neuron: " + input.getName() + " of output neuron: " + neuron +
                        "The input neuron should have been processed before the output neuron - aren't there cycles in the computation graph?");
//                throw new Exception("Input Value missing for input neuron: " + input.getName() + " of output neuron: " + neuron +
//                        ". The input neuron was not processed before the output - aren't there cycles in the computation graph?");
            }
            Value times = weight.value.times(inputValue);
            if (times == null) {
                LOG.severe("Weight-Value dimension mismatch at input neuron: " + input.getName() + " of output neuron: " + neuron);
            }
            inputValues.add(times);
        }

        // check first if the state can be made more efficient
        ActivationFcn.State updatedState = ActivationFcn.State.getState(computationState.getFcnState(), inputValues);
        computationState.setFcnState(updatedState);

        // initialize state values and evaluate
        computationState.initEval(inputValues);
    }
}
