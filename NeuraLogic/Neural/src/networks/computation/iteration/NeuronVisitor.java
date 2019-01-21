package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisiting;
import networks.computation.iteration.actions.WeightUpdater;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.metadata.states.State;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * Abstract representation for all the actions performed on the level of Neurons while iterating ({@link IterationStrategy}.
 * This is a pure container for the action, completely decoupled from the logic of iteration, i.e. no expansion of nodes
 * for iteration should happen here, just an immediate propagation of messages to neighbours (PureVisitor).
 * The logic of the calculation upon the {@link State} of each neuron is further outsourced to {@link #stateVisitor}
 * for extra composition of possible iteration logic.
 *
 * @see StateVisiting
 * @see Neurons
 * @see Neuron
 */
public abstract class NeuronVisitor {

    /**
     * We need the context of the network even at the level of visiting individual neurons to ask for the inputs properly.
     */
    NeuralNetwork<State.Neural.Structure> network;
    /**
     * The logic of the calculation upon the {@link State} of each neuron is further outsourced to {@link #stateVisitor}
     */
    StateVisiting.ComputationVisitor stateVisitor;   //todo change to generic StateVisitor?

    public NeuronVisitor(NeuralNetwork<State.Neural.Structure> network, StateVisiting.ComputationVisitor computationVisitor) {
        this.network = network;
        this.stateVisitor = computationVisitor;
    }

    /**
     * Default double dispatch, some of the specific Neuron classes should be passed as an argument instead!
     *
     * @param neuron
     */
    public void visit(Neurons neuron) {
        Logger LOG = Logger.getLogger(NeuronVisitor.class.getName());
        LOG.severe("Error: Visitor calling a default method through double dispatch.");
    }

    /**
     * Expand neighbours, Propagate result of this neuron into the neighbours (inputs or outputs).
     *
     * @param neuron
     */
    public abstract void visit(Neuron neuron);


    public abstract static class Weighted extends NeuronVisitor {

        /**
         * Takes care of the weight update during neuron visit.
         */
        @Nullable
        WeightUpdater weightUpdater;

        public Weighted(NeuralNetwork<State.Neural.Structure> network, StateVisiting.ComputationVisitor computationVisitor, @Nullable WeightUpdater weightUpdater) {
            super(network, computationVisitor);
            this.weightUpdater = weightUpdater;
        }

        /**
         * Expand neighbours and Propagate result of this neuron into the neighbours (inputs or outputs) with the corresponding edge weight.
         * Also record an update for the weight in the process.
         *
         * @param neuron
         */
        abstract void visit(WeightedNeuron neuron);

    }
}