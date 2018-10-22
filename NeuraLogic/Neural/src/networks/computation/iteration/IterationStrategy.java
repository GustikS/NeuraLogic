package networks.computation.iteration;

import networks.computation.iteration.actions.NeuronVisitor;
import networks.structure.metadata.states.State;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;

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

    /**
     * Passive "Iterator" version - no action taken, just returning next Neuron (for processing, e.g. activation and expansion).
     * @return
     */
    public abstract Neuron<Neuron, State.Computation> next();

    /**
     * Active "Propagator" version - takes care of neighbours expansion and ALSO propagation of Values at the same time.
     * This is more efficient than just returning next neuron for processing, which has to repeat the neighbour exploration.
     */
    public abstract void iterate();

    /**
     * Explore neighbours AND propagate results of this neuron into them
     * @param neuron
     */
    public abstract void expand(Neuron<Neuron, State.Computation> neuron);

    /**
     * Explore neighbours AND propagate results of this neuron into them with the corresponding weights
     * @param neuron
     */
    public abstract void expand(WeightedNeuron<Neuron, State.Computation> neuron);

}