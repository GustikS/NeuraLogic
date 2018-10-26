package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisitor;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * Active "Propagator" visitor - takes care of neighbours expansion and ALSO propagation of Values at the same time.
 * In visitor this can be more efficient than just returning next neuron for processing in iterator, which has to repeat the neighbour exploration.
 */
public abstract class NeuronVisiting<V> extends IterationStrategy<V> {
    private static final Logger LOG = Logger.getLogger(NeuronVisiting.class.getName());

    public NeuronVisiting(StateVisitor<V> stateVisitor, NeuralNetwork<State.Structure> network, Neuron<Neuron, State.Computation> outputNeuron) {
        super(stateVisitor, network, outputNeuron);
    }

    /**
     * Default double dispatch, some of the specific Neuron classes should be passed as an argument instead!
     *
     * @param neuron
     */
    public void expand(Neurons neuron) {
        LOG.severe("Error: Visitor calling a default method through double dispatch.");
    }

    /**
     * Expand neighbors, Propagate result of this neuron into the neighbours (inputs or outputs) AND add queue them for expansion, too.
     *
     * @param neuron
     */
    public abstract void expand(Neuron<Neuron, State.Computation> neuron);

    /**
     * Expand neighbors and Propagate result of this neuron into the neighbours (inputs or outputs) with the corresponding edge weight AND add queue them for expansion, too.
     *
     * @param neuron
     */
    public abstract void expand(WeightedNeuron<Neuron, State.Computation> neuron);

}