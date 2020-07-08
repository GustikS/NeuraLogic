package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import org.jetbrains.annotations.Nullable;
import cz.cvut.fel.ida.neural.networks.computation.iteration.IterationStrategy;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.*;

/**
 * Abstract representation for all the actions performed on the level of Neurons while iterating ({@link IterationStrategy}.
 * This is a pure container for the action, completely decoupled from the logic of iteration, i.e. no expansion of nodes
 * for iteration should happen here, just an immediate propagation of messages to neighbours (PureVisitor).
 * The logic of the calculation upon the {@link State} of each neuron is further outsourced to {@link #stateVisitor}
 * for extra composition of possible iteration logic.
 *
 * @see StateVisiting
 * @see Neurons
 * @see BaseNeuron
 */
public abstract class NeuronVisitor {

    /**
     * We need the context of the network even at the level of visiting individual neurons to ask for the inputs properly.
     */
    protected NeuralNetwork<State.Neural.Structure> network;
    /**
     * The logic of the calculation upon the {@link State} of each neuron is further outsourced to {@link #stateVisitor}
     */
    public StateVisiting.Computation stateVisitor;   //todo change to generic StateVisitor?

    public NeuronVisitor(NeuralNetwork<State.Neural.Structure> network, StateVisiting.Computation computationVisitor) {
        this.network = network;
        this.stateVisitor = computationVisitor;
    }

    /**
     * Expand neighbours, Propagate result of this neuron into the neighbours (inputs or outputs).
     *
     * @param neuron
     */
    public abstract <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron);

    /**
     * Class to inherit for Visitors that want to recognize between weighted and unweighted neurons
     */
    public abstract static class Weighted extends NeuronVisitor {

        /**
         * Takes care of the weight update during neuron visit.
         */
        @Nullable
        public WeightUpdater weightUpdater;

        public Weighted(NeuralNetwork<State.Neural.Structure> network, StateVisiting.Computation computationVisitor, @Nullable WeightUpdater weightUpdater) {
            super(network, computationVisitor);
            this.weightUpdater = weightUpdater;
        }

        /**
         * Expand neighbours and Propagate result of this neuron into the neighbours (inputs or outputs) with the corresponding edge weight.
         * Also record an update for the weight in the process.
         *
         * @param neuron
         */
        public abstract <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron);

        /**
         * To be used if operation specific to individual neuron classes (other than just recognizing un/weighted neurons) is necessary
         */
        public abstract static class Detailed extends Weighted {

            public Detailed(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor, WeightUpdater weightUpdater) {
                super(network, computationVisitor, weightUpdater);
            }

            public abstract <S extends State.Neural> void visit(AggregationNeuron<S> neuron);

            public abstract <S extends State.Neural> void visit(RuleNeuron<S> neuron);

            public abstract <S extends State.Neural> void visit(WeightedRuleNeuron<S> neuron);

            public abstract <S extends State.Neural> void visit(WeightedAtomNeuron<S> neuron);

            public abstract <S extends State.Neural> void visit(AtomNeuron<S> neuron);

            public abstract void visit(FactNeuron neuron);
        }

        public abstract static class Indexed extends Weighted {

            public Indexed(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor, @Nullable WeightUpdater weightUpdater) {
                super(network, computationVisitor, weightUpdater);
            }

            public void visit(WeightedNeuron neuron) {
                visit(neuron, 0);
            }

            public void visit(BaseNeuron neuron) {
                visit(neuron, 0);
            }

            abstract void visit(BaseNeuron neuron, int index);

            abstract void visit(WeightedNeuron neuron, int index);

        }

    }
}