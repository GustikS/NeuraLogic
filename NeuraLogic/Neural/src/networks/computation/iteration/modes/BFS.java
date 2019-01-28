package networks.computation.iteration.modes;

import ida.utils.tuples.Pair;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.NeuronIterating;
import networks.computation.iteration.NeuronVisiting;
import networks.computation.iteration.TopDown;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * BottomUp post-order is a problem here, we would have to misuse parentcounters so that a node is added for expansion
 * only by the LAST parent - otherwise it would be added to queue too early and the reverse iteration would not be post-order,
 * i.e. it would not hold that all children are processed before all parents.
 *
 * - Actually, that's Kahn’s algorithm (using BFS for topologic sort), for which we need to track and iteratively decrease
 *  the in-degrees of nodes, which makes it less efficient than DFS (although asymptotically it's the same)
 *
 */

public class BFS {
    private static final Logger LOG = Logger.getLogger(BFS.class.getName());

    Queue<Neuron<Neuron, State.Neural>> queue;

    public class TDownIterator extends NeuronIterating implements TopDown {

        public TDownIterator(NeuralNetwork<State.Neural.Structure> network, Neuron<Neuron, State.Neural> neuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, neuron, pureNeuronVisitor);
            queue = new ArrayDeque<>(network.getSize());
            queue.add(outputNeuron);
        }

        @Override
        public Neuron<Neuron, State.Neural> next() {
            while (!queue.isEmpty()) {
                Neuron<Neuron, State.Neural> poll = queue.poll();
                if (poll.getComputationView(neuronVisitor.stateVisitor.stateIndex).ready4expansion(neuronVisitor.stateVisitor)) {
                    Iterator<Neuron> inputs = network.getInputs(poll);
                    for (Neuron next; (next = inputs.next()) != null; ) {
                        queue.add(next);
                    }
                    return poll;
                }
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public void topdown() {
            iterate();
        }
    }

    public class TDownVisitor extends NeuronVisiting.Weighted implements TopDown {
        StateVisiting.Computation stateVisitor;
        WeightUpdater weightUpdater;
        StateVisiting.Computation bottomUp;

        public TDownVisitor(NeuralNetwork<State.Neural.Structure> network, Neuron<Neuron, State.Neural> neuron, StateVisiting.Computation topDown, StateVisiting.Computation bottomUp, WeightUpdater weightUpdater) {
            super(network, neuron);
            queue = new ArrayDeque<>(network.getSize());
            queue.add(outputNeuron);
            this.bottomUp = bottomUp;
            this.weightUpdater = weightUpdater;
            this.stateVisitor = topDown;
        }

        @Override
        public void visit(Neuron<Neuron, State.Neural> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value value = stateVisitor.visit(state);
            Iterator<Neuron> inputs = network.getInputs(neuron);
            for (Neuron input; (input = inputs.next()) != null; ) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                computationView.store(stateVisitor, value);
                if (computationView.ready4expansion(stateVisitor)) {    //we can check the children in advance here since we expanded them already ourselves
                    queue.add(input);
                }
            }
        }

        @Override
        public void visit(WeightedNeuron<Neuron, State.Neural> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            Pair<Iterator<Neuron>, Iterator<Weight>> inputs = network.getInputs(neuron);

            weightUpdater.visit(neuron.offset, gradient);

            Iterator<Neuron> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            Neuron input;
            Weight weight;
            while ((input = inputNeurons.next()) != null && (weight = inputWeights.next()) != null) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);

                weightUpdater.visit(weight, gradient.times(computationView.getResult(bottomUp)));
                computationView.store(stateVisitor, gradient.times(weight.value));

                if (computationView.ready4expansion(stateVisitor)) {
                    queue.add(input);
                }
            }
        }

        @Override
        public void topdown() {
            while (!queue.isEmpty()) {
                Neuron<Neuron, State.Neural> neuron = queue.poll();
                neuron.visit(this);
            }
        }
    }
}