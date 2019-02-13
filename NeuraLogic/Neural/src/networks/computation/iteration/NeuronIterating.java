package networks.computation.iteration;

import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.metadata.states.State;

/**
 * Iteration strategy {@link IterationStrategy} based on the clean Iterator pattern. I.e. we are traversing the structure
 * of {@link NeuralNetwork} and returning the elements one-by-one, to be visited by {@link #neuronVisitor}.
 */
public abstract class NeuronIterating extends IterationStrategy implements NeuronIterator {

    /**
     * Takes care of aggregating/propagating values to neighbours
     */
    protected NeuronVisitor neuronVisitor;

    public NeuronIterating(NeuralNetwork<State.Neural.Structure> network, BaseNeuron<BaseNeuron, State.Neural> outputNeuron, NeuronVisitor pureNeuronVisitor) {
        super(network, outputNeuron);
        this.neuronVisitor = pureNeuronVisitor;
    }

    /**
     * A default implementation for a typical NeuronIterating. Can be called for both BUp&TDown directions,
     * since the directionality is taken care of by the next() method.
     */
    @Override
    public void iterate() {
        while (hasNext()){
            BaseNeuron<BaseNeuron, State.Neural> nextNeuron = next();
            nextNeuron.visit(neuronVisitor);
        }
    }
}