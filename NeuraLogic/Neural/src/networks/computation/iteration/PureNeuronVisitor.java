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

    StateVisiting.ComputationVisitor computationVisitor;   //todo test copy this to inner class for performance?
    NeuralNetwork<State.Neural.Structure> network;

    public PureNeuronVisitor(){

    }

    public PureNeuronVisitor(StateVisiting.ComputationVisitor computationVisitor, NeuralNetwork<State.Neural.Structure> network) {
        this.computationVisitor = computationVisitor;
        this.network = network;
    }

    public class Up extends PureNeuronVisitor implements NeuronVisitor.Weighted {

        public Up(StateVisiting.ComputationVisitor stateVisitor, NeuralNetwork<State.Neural.Structure> network) {
            super(stateVisitor, network);
        }

        @Override
        public void visit(Neuron neuron) {
            State.Neural.Computation state = neuron.getComputationView(computationVisitor.stateIndex);
            Iterator<Neuron> inputs = network.getInputs(neuron);
            for (Neuron input; (input = inputs.next()) != null; ) {
                state.store(computationVisitor, input.getComputationView(computationVisitor.stateIndex).getResult(computationVisitor));
            }
            Value value = computationVisitor.visit(state);
        }

        @Override
        public void visit(WeightedNeuron neuron) {
            State.Neural.Computation state = neuron.getComputationView(computationVisitor.stateIndex);
            Pair<Iterator<Neuron>, Iterator<Weight>> inputs = network.getInputs(neuron);
            Iterator<Neuron> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            Neuron input;
            Weight weight;

            state.store(computationVisitor, neuron.offset.value);       //todo invalidate state first, or is it ensured that it is invalidated?
            while ((input = inputNeurons.next()) != null && (weight = inputWeights.next()) != null) { //todo test version with fori
                state.store(computationVisitor, input.getComputationView(computationVisitor.stateIndex).getResult(computationVisitor).times(weight.value));
            }
            Value value = computationVisitor.visit(state);
        }
    }

    public class Down extends PureNeuronVisitor implements NeuronVisitor.Weighted {

        public Down(StateVisiting.ComputationVisitor stateVisitor, NeuralNetwork<State.Neural.Structure> network) {
            super(stateVisitor, network);
        }

        @Override
        public void visit(Neuron neuron) {
            State.Neural.Computation state = neuron.getComputationView(computationVisitor.stateIndex);
            Value value = computationVisitor.visit(state);
            Iterator<Neuron> inputs = network.getInputs(neuron);
            for (Neuron input; (input = inputs.next()) != null; ) {
                input.getComputationView(computationVisitor.stateIndex).store(computationVisitor, value);
            }
        }

        @Override
        public void visit(WeightedNeuron neuron) {
            State.Neural.Computation state = neuron.getComputationView(computationVisitor.stateIndex);
            Value value = computationVisitor.visit(state);
            Pair<Iterator<Neuron>, Iterator<Weight>> inputs = network.getInputs(neuron);//todo next offset?! - not needed for gradient I guess?
            Iterator<Neuron> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            Neuron input;
            Weight weight;
            while ((input = inputNeurons.next()) != null && (weight = inputWeights.next()) != null) {
                input.getComputationView(computationVisitor.stateIndex).store(computationVisitor, value.times(weight.value));
            }
        }
    }
}
