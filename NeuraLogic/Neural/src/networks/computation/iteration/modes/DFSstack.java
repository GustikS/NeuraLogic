package networks.computation.iteration.modes;

import ida.utils.tuples.Pair;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.BottomUp;
import networks.computation.iteration.NeuronIterating;
import networks.computation.iteration.NeuronVisiting;
import networks.computation.iteration.TopDown;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Iterative DFS pre-order and post-order (!= reverse pre-order - needs extra treatment with boolean flags).
 *
 * This mode is good for iterators, as they are quite naturally working with Stacks, but it is less suited for visitors,
 * which basically behave like iterators here, since we are explicitly iterating the stack.
 */
public class DFSstack {
    private static final Logger LOG = Logger.getLogger(DFSstack.class.getName());

    private Deque<BaseNeuron<BaseNeuron, State.Neural>> stack;

    public class TDownIterator extends NeuronIterating implements TopDown {

        public TDownIterator(NeuralNetwork<State.Neural.Structure> network, BaseNeuron<BaseNeuron, State.Neural> neuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, neuron, pureNeuronVisitor);
            stack = new ArrayDeque<>();
            stack.push(outputNeuron);
        }

        public BaseNeuron<BaseNeuron, State.Neural> next() {
            while (!stack.isEmpty()) {
                BaseNeuron<BaseNeuron, State.Neural> pop = stack.pop();
                if (pop.getComputationView(neuronVisitor.stateVisitor.stateIndex).ready4expansion(neuronVisitor.stateVisitor)) {  //we must check the parent here (i.e. this is correct) - it would not put the state on Stack when it should with the last call if checking the children forward instead
                    Iterator<BaseNeuron> inputs = network.getInputs(pop);
                    for (BaseNeuron next; (next = inputs.next()) != null; ) {
                        stack.add(next);
                    }
                    return pop;
                }
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
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

        public TDownVisitor(NeuralNetwork<State.Neural.Structure> network, BaseNeuron<BaseNeuron, State.Neural> neuron, StateVisiting.Computation topDown, StateVisiting.Computation bottomUp, WeightUpdater weightUpdater) {
            super(network, neuron);
            stack = new ArrayDeque<>();
            stack.push(outputNeuron);
            this.bottomUp = bottomUp;
            this.weightUpdater = weightUpdater;
            this.stateVisitor = topDown;
        }

        @Override
        public void visit(BaseNeuron<BaseNeuron, State.Neural> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value value = stateVisitor.visit(state);
            Iterator<BaseNeuron> inputs = network.getInputs(neuron, state.getAggregationState().getInputMask());
            for (BaseNeuron input; (input = inputs.next()) != null; ) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                computationView.store(stateVisitor, value);
                if (computationView.ready4expansion(stateVisitor)) {    //we can check the children in advance here since we expanded them already ourselves
                    stack.push(input);
                }
            }
        }

        @Override
        public void visit(WeightedNeuron<BaseNeuron, State.Neural> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            Pair<Iterator<BaseNeuron>, Iterator<Weight>> inputs = network.getInputs(neuron, state.getAggregationState().getInputMask());

            weightUpdater.visit(neuron.offset, gradient);

            Iterator<BaseNeuron> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            BaseNeuron input;
            Weight weight;
            while ((input = inputNeurons.next()) != null && (weight = inputWeights.next()) != null) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);

                weightUpdater.visit(weight, gradient.times(computationView.getResult(bottomUp)));
                computationView.store(stateVisitor, gradient.times(weight.value));

                if (computationView.ready4expansion(stateVisitor)) {
                    stack.push(input);
                }
            }
        }

        @Override
        public void topdown() {
            while (!stack.isEmpty()) {
                BaseNeuron<BaseNeuron, State.Neural> neuron = stack.pop();
                neuron.visit(this);
            }
        }
    }

    /**
     * todo test if correct
     *
     * @return
     */
    public class BUpIterator extends NeuronIterating implements BottomUp<Value> {

        StateVisiting.Computation stateVisitor = neuronVisitor.stateVisitor;

        /**
         * In iterative post-order (=topologic order) we need to know that we finished processing all the children.
         * For that we push each neuron on stack twice - pre&post order - first time for expansion only, second time with a ready4processing flag.
         */
        Deque<Pair<Boolean, BaseNeuron<BaseNeuron, State.Neural>>> postStack;

        public BUpIterator(NeuralNetwork<State.Neural.Structure> network, BaseNeuron<BaseNeuron, State.Neural> neuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, neuron, pureNeuronVisitor);
            postStack = new ArrayDeque<>(network.getSize());
            postStack.push(new Pair<>(false, outputNeuron));
        }

        public BaseNeuron<BaseNeuron, State.Neural> next() {
            while (!postStack.isEmpty()) {
                Pair<Boolean, BaseNeuron<BaseNeuron, State.Neural>> current = postStack.poll();
                if (current.r){ //post-order = ready 4 processing
                    return current.s; // will perform mark as visited
                }
                postStack.push(new Pair<>(true,current.s));
                Iterator<BaseNeuron> inputs = network.getInputs(current.s);
                for (BaseNeuron input; (input = inputs.next()) != null; ) {
                    if (!input.getComputationView(stateVisitor.stateIndex).ready4expansion(stateVisitor))   // if not yet calculated (stateVisitor = Evaluator)
                        postStack.push(new Pair<>(false, input));
                }
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return !postStack.isEmpty();
        }

        @Override
        public Value bottomUp() {
            iterate();
            return outputNeuron.getComputationView(neuronVisitor.stateVisitor.stateIndex).getResult(neuronVisitor.stateVisitor);
        }
    }

    /**
     * There is no advantage in bottom-up visitor as it needs to do post-order traversal, it is just the same as iterator + subsequent processing of each neuron.
     */
    @Deprecated
    public class BUpVisitor extends NeuronVisiting.Weighted implements BottomUp<Value> {

        /**
         * Misusing the iteration from iterator.
         */
        BUpIterator bUpStackIterator;

        NeuronVisitor.Weighted neuronVisitor;

        public BUpVisitor(NeuralNetwork<State.Neural.Structure> network, BaseNeuron<BaseNeuron, State.Neural> neuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, neuron);
            bUpStackIterator = new BUpIterator(network, neuron, pureNeuronVisitor);
            neuronVisitor = pureNeuronVisitor;

        }

        @Override
        public Value bottomUp() {
            while (bUpStackIterator.hasNext()) {
                BaseNeuron<BaseNeuron, State.Neural> next = bUpStackIterator.next();
                next.visit(neuronVisitor);
            }
            return outputNeuron.getComputationView(neuronVisitor.stateVisitor.stateIndex).getResult(neuronVisitor.stateVisitor);
        }

        @Override
        public void visit(BaseNeuron<BaseNeuron, State.Neural> neuron) {
            neuronVisitor.visit(neuron);
        }

        @Override
        public void visit(WeightedNeuron<BaseNeuron, State.Neural> neuron) {
            neuronVisitor.visit(neuron);
        }
    }
}