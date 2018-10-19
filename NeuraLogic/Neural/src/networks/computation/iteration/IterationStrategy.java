package networks.computation.iteration;

import networks.computation.iteration.actions.NeuronVisitor;
import networks.structure.metadata.states.State;
import networks.structure.networks.NeuralNetwork;
import networks.structure.neurons.Neuron;

import java.util.Iterator;

/**
 * Process neurons given some particular iteration order and given the direction of either:
 * - top-down (starting from queryNeuron and going down, e.g. backpropagation)
 * - or bottom-up (may also start from query neuron, but returns neuron to perform the action when going up, e.g. evaluation)
 * <p>
 * edit - it cannot be just returning next() Neuron as an Iterator. This class will have to also perform the action itself through StateVisitor.
 * - this will save calling for the inputs of a neuron twice, since inputs() are needed to both update the state (propagate gradient) and expand to next neurons.
 * - otherwise it would just return the next neuron (with a ready() state), and on that it would propagate the state info further
 *
 * @param <N>
 */
public abstract class IterationStrategy<N extends State.Structure, V> implements Iterator<Neuron<Neuron, State.Computation>> {

    NeuronVisitor<V> neuronVisitor;
    NeuralNetwork<N> network;
    Neuron<Neuron, State.Computation> outputNeuron;

    public IterationStrategy(NeuronVisitor<V> neuronVisitor, NeuralNetwork<N> network, Neuron<Neuron, State.Computation> outputNeuron) {
        this.neuronVisitor = neuronVisitor;
        this.network = network;
        this.outputNeuron = outputNeuron;
    }
}