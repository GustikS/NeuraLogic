package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisitor;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;

/**
 * Process neurons given some particular iteration order and given the direction of either:
 * - top-down (starting from queryNeuron and going down, e.g. backpropagation)
 * - or bottom-up (may also start from query neuron, but returns neuron to perform the action when going up, e.g. evaluation)
 * <p>
 * edit - it cannot be just returning next() Neuron as an Iterator. This class will have to also perform the action itself through StateVisitor.
 * - this will save calling for the inputs of a neuron twice, since inputs() are needed to both update the state (propagate gradient) and expand to next neurons.
 * - otherwise it would just return the next neuron (with a ready() state), and on that it would propagate the state info further
 *
 */
public abstract class NeuronIterating<V> extends IterationStrategy<V> implements NeuronIterator {

    /**
     * Takes care of propagating values to neighbours
     */
    NeuronVisitor pureNeuronVisitor;

    public NeuronIterating(StateVisitor<V> stateVisitor, NeuralNetwork<State.Structure> network, Neuron<Neuron, State.Computation> outputNeuron, NeuronVisitor pureNeuronVisitor) {
        super(stateVisitor, network, outputNeuron);
        this.pureNeuronVisitor = pureNeuronVisitor;
    }

    @Override
    public void iterate() {
        while (hasNext()){
            Neuron<Neuron, State.Computation> nextNeuron = next();
            nextNeuron.propagate(pureNeuronVisitor);
        }
    }
}