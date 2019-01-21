package networks.computation.iteration;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;

/**
 * Iteration strategy {@link IterationStrategy} based on the clean Iterator pattern. I.e. we are traversing the structure
 * of {@link NeuralNetwork} and returning the elements one-by-one, to be visited by {@link #neuronVisitor}.
 */
public abstract class NeuronIterating extends IterationStrategy implements NeuronIterator {

    public NeuronIterating(NeuralNetwork<State.Neural.Structure> network, Neuron<Neuron, State.Neural> outputNeuron, NeuronVisitor pureNeuronVisitor) {
        super(network, outputNeuron, pureNeuronVisitor);
    }

    /**
     * A default implementation for a typical NeuronIterating. Can be called for both BUp&TDown directions,
     * since the directionality is taken care of by the next() method.
     */
    @Override
    public void iterate() {
        while (hasNext()){
            Neuron<Neuron, State.Neural> nextNeuron = next();
            nextNeuron.visit(neuronVisitor);
        }
    }
}