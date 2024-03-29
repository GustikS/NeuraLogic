package cz.cvut.fel.ida.neural.networks.computation.iteration.modes;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.NeuronIterating;
import cz.cvut.fel.ida.neural.networks.computation.iteration.NeuronVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.TopDown;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * BottomUp post-order is a problem here, we would have to misuse parentcounters so that a node is added for expansion
 * only by the LAST parent - otherwise it would be added to queue too early and the reverse iteration would not be post-order,
 * i.e. it would not hold that all children are processed before all parents.
 * <p>
 * - Actually, that's Kahn’s algorithm (using BFS for topologic sort), for which we need to track and iteratively decrease
 * the in-degrees of nodes, which makes it less efficient than DFS (although asymptotically it's the same)
 */

public class BFS {
    private static final Logger LOG = Logger.getLogger(BFS.class.getName());

    Queue<Neurons<Neurons, State.Neural>> queue;

    public class TDownIterator extends NeuronIterating implements TopDown {

        public TDownIterator(NeuralNetwork<State.Neural.Structure> network, Neurons neuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, neuron, pureNeuronVisitor);
            queue = new ArrayDeque<>(network.getNeuronCount());
            queue.add(outputNeuron);
        }

        @Override
        public BaseNeuron<Neurons, State.Neural> next() {
            while (!queue.isEmpty()) {
                BaseNeuron<Neurons, State.Neural> poll = (BaseNeuron<Neurons, State.Neural>) queue.poll();
                if (poll.getComputationView(neuronVisitor.stateVisitor.stateIndex).ready4expansion(neuronVisitor.stateVisitor)) {
                    Iterator<Neurons> inputs = network.getInputs(poll);
                    while (inputs.hasNext()) {
                        Neurons next = inputs.next();
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

        public TDownVisitor(NeuralNetwork<State.Neural.Structure> network, Neurons neuron, StateVisiting.Computation topDown, WeightUpdater weightUpdater) {
            super(network, neuron);
            queue = new ArrayDeque<>(network.getNeuronCount());
            queue.add(outputNeuron);
            this.weightUpdater = weightUpdater;
            this.stateVisitor = topDown;
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            Iterator<T> inputs = network.getInputs(neuron, state.getFcnState().getInputMask());
            while (inputs.hasNext()) {
                T input = inputs.next();
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                computationView.storeGradient(gradient);
                if (computationView.ready4expansion(stateVisitor)) {    //we can check the children in advance here since we expanded them already ourselves
                    queue.add(input);
                }
            }
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron, state.getFcnState().getInputMask());

            weightUpdater.visit(neuron.offset, gradient);

            Iterator<T> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            T input;
            Weight weight;
            while (inputNeurons.hasNext() && inputWeights.hasNext()) {
                input = inputNeurons.next();
                weight = inputWeights.next();

                State.Neural.Computation inputComputationView = input.getComputationView(stateVisitor.stateIndex);

                Value transpInputValue = inputComputationView.getValue().transposedView();

                weightUpdater.visit(weight, gradient.times(transpInputValue));
                inputComputationView.storeGradient(gradient.transposedView().times(weight.value));

                if (inputComputationView.ready4expansion(stateVisitor)) {
                    queue.add(input);
                }
            }
        }

        @Override
        public void topdown() {
            while (!queue.isEmpty()) {
                Neurons<Neurons, State.Neural> neuron = queue.poll();
                neuron.visit(this);
            }
        }

    }
}