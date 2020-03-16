package cz.cvut.fel.ida.neural.networks.computation.iteration;

import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

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

    public NeuronVisiting(NeuralNetwork<State.Neural.Structure> network, Neurons outputNeuron) {
        super(network, outputNeuron);
    }

    /**
     * Expand neighbors, Propagate result of this neuron into the neighbours (inputs or outputs) AND add queue them for expansion, too.
     *
     * @param neuron
     */
    public abstract <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T,S> neuron);

    public static abstract class Weighted extends NeuronVisiting {

        public Weighted(NeuralNetwork<State.Neural.Structure> network, Neurons outputNeuron) {
            super(network, outputNeuron);
        }

        /**
         * Expand neighbors and Propagate result of this neuron into the neighbours (inputs or outputs) with the corresponding edge weight AND add queue them for expansion, too.
         *
         * @param neuron
         */
        public abstract <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T,S> neuron);
    }
}