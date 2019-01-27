package networks.computation.iteration;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * Iteration strategy {@link IterationStrategy} based on the Visitor pattern. I.e. we are traversing the structure
 * of {@link NeuralNetwork} AND simultaneously performing action of visiting.
 *
 * remarks:
 * Active "Propagator" visitor - takes care of neighbours expansion and ALSO propagation of Values at the same time.
 * In visitor pattern this can be more efficient than just returning next neuron for processing in iterator, which has to repeat the neighbour exploration.
 *  //todo test - we end up repeating a lot of the code from networks.computation.iteration.visitors.neurons.StandardNeuronVisitors here just ot avoid repeating the iteration - is that really necessary?
 */
public abstract class NeuronVisiting extends IterationStrategy {
    private static final Logger LOG = Logger.getLogger(NeuronVisiting.class.getName());

    public NeuronVisiting(NeuralNetwork<State.Neural.Structure> network, Neuron<Neuron, State.Neural> outputNeuron) {
        super(network, outputNeuron);
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

    public static abstract class Weighted extends NeuronVisiting {

        public Weighted(NeuralNetwork<State.Neural.Structure> network, Neuron<Neuron, State.Neural> outputNeuron) {
            super(network, outputNeuron);
        }

        /**
         * Expand neighbors and Propagate result of this neuron into the neighbours (inputs or outputs) with the corresponding edge weight AND add queue them for expansion, too.
         *
         * @param neuron
         */
        public abstract void visit(WeightedNeuron<Neuron, State.Neural> neuron);
    }
}