package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisiting;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * Active "Propagator" visitor - takes care of neighbours expansion and ALSO propagation of Values at the same time.
 * In visitor pattern this can be more efficient than just returning next neuron for processing in iterator, which has to repeat the neighbour exploration.
 */
public abstract class NeuronVisiting<V> extends IterationStrategy<V> {
    private static final Logger LOG = Logger.getLogger(NeuronVisiting.class.getName());

    public NeuronVisiting(StateVisiting<V> stateVisitor, NeuralNetwork<State.Neural.Structure> network, Neuron<Neuron, State.Neural> outputNeuron) {
        super(stateVisitor, network, outputNeuron);
    }

    /**
     * Default double dispatch, some of the specific Neuron classes should be passed as an argument instead!
     *
     * @param neuron
     */
    public void visit(Neurons neuron) {
        LOG.severe("Error: Visitor calling a default method through double dispatch.");
    }

    /**
     * Expand neighbors, Propagate result of this neuron into the neighbours (inputs or outputs) AND add queue them for expansion, too.
     *
     * @param neuron
     */
    public abstract void visit(Neuron<Neuron, State.Neural> neuron);
}