package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisitor;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;


public abstract class NeuronIterating<V> extends IterationStrategy<V> implements NeuronIterator {

    /**
     * Takes care of propagating values to neighbours
     */
    NeuronVisitor pureNeuronVisitor;

    public NeuronIterating(StateVisitor<V> stateVisitor, NeuralNetwork<State.Structure> network, Neuron<Neuron, State.Computation> outputNeuron, NeuronVisitor pureNeuronVisitor) {
        super(stateVisitor, network, outputNeuron);
        this.pureNeuronVisitor = pureNeuronVisitor;
    }

    /**
     * A default implementation for a typical NeuronIterating. Can be called for both BUp&TDown directions,
     * since the directionality is taken care of by the next() method.
     */
    @Override
    public void iterate() {
        while (hasNext()){
            Neuron<Neuron, State.Computation> nextNeuron = next();
            nextNeuron.propagate(pureNeuronVisitor);
        }
    }
}