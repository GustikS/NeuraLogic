package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisitor;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public abstract class IterationStrategy<V> implements DirectedIteration {
    private static final Logger LOG = Logger.getLogger(IterationStrategy.class.getName());

    /**
     * Needed for ready4expansion check during iteration. Only neuron that are ready 4 processing must be returned,
     * since some of the neurons on stack are put there just for iteration purposes.
     */
    StateVisitor<V> stateVisitor;
    NeuralNetwork<State.Structure> network;
    Neuron<Neuron, State.Computation> outputNeuron;

    public IterationStrategy(StateVisitor<V> stateVisitor, NeuralNetwork<State.Structure> network, Neuron<Neuron, State.Computation> outputNeuron) {
        this.stateVisitor = stateVisitor;
        this.network = network;
        this.outputNeuron = outputNeuron;
    }
}