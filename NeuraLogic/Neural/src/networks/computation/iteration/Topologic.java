package networks.computation.iteration;

import ida.utils.tuples.Pair;
import networks.computation.iteration.actions.NeuronVisitor;
import networks.computation.training.evaluation.values.Value;
import networks.structure.metadata.states.State;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Topologic extends IterationStrategy<TopologicNetwork<State.Structure>> {
    private static final Logger LOG = Logger.getLogger(Topologic.class.getName());

    public Topologic(NeuronVisitor neuronVisitor) {
        super(neuronVisitor);
    }

    @Override
    public Value topDown(Neuron<?, ?> neuron, TopologicNetwork<State.Structure> network) {
        for (int i = network.allNeuronsTopologic.size() - 1; i >= 0; i--) {
            Neuron<Neuron, State.Computation> actual = network.allNeuronsTopologic.get(i);
            Value value = actual.state.getInputSum(neuronVisitor);
            Iterator<Neuron> inputs = network.getInputs(actual);
            for (Neuron input; (input = inputs.next()) != null; ) {
                neuronVisitor.propagate(value, input.state);
            }
        }
        return neuron.state;
    }

    /**
     * Simple left -> right iteration over the stored topologically sorted array of neurons from TopologicNetwork
     *
     * @param neuron
     * @param network
     * @return
     */
    public Value bottomUp(TopologicNetwork<State.Structure> network, Neuron<?, ?> neuron) {
        for (int i = 0; i < network.allNeuronsTopologic.size(); i++) {
            Neuron<Neuron, State.Computation> actual = network.allNeuronsTopologic.get(i);
            neuronVisitor.loadStateFromInputs(actual, network);
            neuronVisitor.activateOutput(actual);
        }
        return neuronVisitor.getValue(neuron);
    }

    /**
     * todo this should go to Evaluator
     * @param actual
     * @param network
     */
    private void loadStateFromInputs(Neuron<Neuron, State.Computation> actual, TopologicNetwork<State.Structure> network) {

        Iterator<Neuron> inputs = network.getInputs(actual);
        for (Neuron input; (input = inputs.next()) != null; ) {
            neuronVisitor.propagate(input.state.getInputSum(neuronVisitor), actual.state);
        }
    }

    private void loadStateFromInputs(WeightedNeuron<Neuron, State.Computation> actual, TopologicNetwork<State.Structure> network) {

        Iterator<Pair<Neuron, Weight>> inputs = network.getInputs(actual);
        for (Pair<Neuron, Weight> input; (input = inputs.next()) != null; ) {
            neuronVisitor.propagate(input.state.getInputSum(neuronVisitor), actual.state);
        }
    }


    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Neuron<?, ?> next() {
        return null;
    }

    @Override
    public Value bottomUp(TopologicNetwork<State.Structure> network, Neuron<?, ?> outputNeuron, List<Neuron<?, ?>> leafs) {
        return null;
    }

}