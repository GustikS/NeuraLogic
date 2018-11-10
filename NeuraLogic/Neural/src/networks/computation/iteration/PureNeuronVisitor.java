package networks.computation.iteration;

import ida.utils.tuples.Pair;
import networks.computation.iteration.actions.StateVisiting;
import networks.computation.evaluation.values.Value;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.Iterator;

/**
 * The classic implementation of propagating Activated State Output into inputs (Down) - e.g., backpropagation
 * and propagating Activated State Output from inputs (Up) and Activation - e.g., evaluation
 */
public class PureNeuronVisitor {

    StateVisiting.ComputationVisitor stateVisitor;   //todo test copy this to inner class for performance?
    NeuralNetwork<State.Structure> network;

    public PureNeuronVisitor(){

    }

    public PureNeuronVisitor(StateVisiting.ComputationVisitor stateVisitor, NeuralNetwork<State.Structure> network) {
        this.stateVisitor = stateVisitor;
        this.network = network;
    }

    public class Up extends PureNeuronVisitor implements NeuronVisitor.Weighted {

        public Up(StateVisiting.ComputationVisitor stateVisitor, NeuralNetwork<State.Structure> network) {
            super(stateVisitor, network);
        }

        @Override
        public void visit(Neuron neuron) {
            Iterator<Neuron> inputs = network.getInputs(neuron);
            State.Computation state = neuron.getStateView(stateVisitor.stateIndex);
            for (Neuron input; (input = inputs.next()) != null; ) {
                state.store(stateVisitor, input.getStateView(stateVisitor.stateIndex).getResult(stateVisitor));
            }
            Value value = stateVisitor.activateOutput(state, neuron.activation);
        }

        @Override
        public void visit(WeightedNeuron neuron) {
            Pair<Iterator<Neuron>, Iterator<Weight>> inputs = network.getInputs(neuron);
            Iterator<Neuron> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            Neuron input;
            Weight weight;
            neuron.state.store(stateVisitor, neuron.offset.value);       //todo invalidate state first, or is it ensured that it is invalidated?
            while ((input = inputNeurons.next()) != null && (weight = inputWeights.next()) != null) { //todo test version with fori
                neuron.state.store(stateVisitor, input.state.getResult(stateVisitor), weight);
            }
            Value value = neuron.state.activateOutput(stateVisitor, neuron.activation);
        }
    }

    public class Down extends PureNeuronVisitor implements NeuronVisitor.Weighted {

        public Down(StateVisiting.ComputationVisitor stateVisitor, NeuralNetwork<State.Structure> network) {
            super(stateVisitor, network);
        }

        @Override
        public void visit(Neuron neuron) {
            Value value = neuron.state.activateOutput(stateVisitor, neuron.activation);
            Iterator<Neuron> inputs = network.getInputs(neuron);
            for (Neuron input; (input = inputs.next()) != null; ) {
                input.state.store(stateVisitor, value);
            }
        }

        @Override
        public void visit(WeightedNeuron neuron) {
            Value value = neuron.state.activateOutput(stateVisitor, neuron.activation);
            Pair<Iterator<Neuron>, Iterator<Weight>> inputs = network.getInputs(neuron);//todo next offset?!
            Iterator<Neuron> inputNeurons = inputs.r;
            Iterator<Weight> inputWieghts = inputs.s;
            Neuron input;
            Weight weight;
            while ((input = inputNeurons.next()) != null && (weight = inputWieghts.next()) != null) {
                input.state.store(stateVisitor, value, weight);
            }
        }
    }
}
