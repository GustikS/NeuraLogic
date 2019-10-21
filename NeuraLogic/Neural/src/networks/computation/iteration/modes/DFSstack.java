package networks.computation.iteration.modes;

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
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;
import utils.generic.Pair;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Iterative DFS pre-order and post-order (!= reverse pre-order - needs extra treatment with boolean flags).
 * <p>
 * This mode is good for iterators, as they are quite naturally working with Stacks, but it is less suited for visitors,
 * which basically behave like iterators here, since we are explicitly iterating the stack.
 */
public class DFSstack {
    private static final Logger LOG = Logger.getLogger(DFSstack.class.getName());

    private Deque<Neurons<Neurons, State.Neural>> stack;

    public class TDownIterator extends NeuronIterating implements TopDown {

        public TDownIterator(NeuralNetwork<State.Neural.Structure> network, Neurons neuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, neuron, pureNeuronVisitor);
            stack = new ArrayDeque<>();
            stack.push(outputNeuron);
        }

        public BaseNeuron<Neurons, State.Neural> next() {
            while (!stack.isEmpty()) {
                BaseNeuron<Neurons, State.Neural> pop = (BaseNeuron<Neurons, State.Neural>) stack.pop();
                if (pop.getComputationView(neuronVisitor.stateVisitor.stateIndex).ready4expansion(neuronVisitor.stateVisitor)) {  //we must check the parent here (i.e. this is correct) - it would not put the state on Stack when it should with the last call if checking the children forward instead
                    Iterator<Neurons> inputs = network.getInputs(pop);
                    for (Neurons next; (next = inputs.next()) != null; ) {
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

        public TDownVisitor(NeuralNetwork<State.Neural.Structure> network, Neurons neuron, StateVisiting.Computation topDown, WeightUpdater weightUpdater) {
            super(network, neuron);
            stack = new ArrayDeque<>();
            stack.push(outputNeuron);
            this.weightUpdater = weightUpdater;
            this.stateVisitor = topDown;
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value value = stateVisitor.visit(state);
            Iterator<T> inputs = network.getInputs(neuron, state.getAggregationState().getInputMask());
            for (T input; (input = inputs.next()) != null; ) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);
                computationView.storeGradient(value);
                if (computationView.ready4expansion(stateVisitor)) {    //we can check the children in advance here since we expanded them already ourselves
                    stack.push(input);
                }
            }
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron, state.getAggregationState().getInputMask());

            weightUpdater.visit(neuron.offset, gradient);

            Iterator<T> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            T input;
            Weight weight;
            while ((input = inputNeurons.next()) != null && (weight = inputWeights.next()) != null) {
                State.Neural.Computation computationView = input.getComputationView(stateVisitor.stateIndex);

                weightUpdater.visit(weight, gradient.times(computationView.getValue()));
                computationView.storeGradient(gradient.times(weight.value));

                if (computationView.ready4expansion(stateVisitor)) {
                    stack.push(input);
                }
            }
        }

        @Override
        public void topdown() {
            while (!stack.isEmpty()) {
                Neurons<Neurons, State.Neural> neuron = stack.pop();
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
        Deque<Pair<Boolean, Neurons<Neurons, State.Neural>>> postStack;

        public BUpIterator(NeuralNetwork<State.Neural.Structure> network, Neurons neuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, neuron, pureNeuronVisitor);
            postStack = new ArrayDeque<>(network.getNeuronCount());
            postStack.push(new Pair<>(false, outputNeuron));
        }

        public BaseNeuron<Neurons, State.Neural> next() {
            while (!postStack.isEmpty()) {
                Pair<Boolean, Neurons<Neurons, State.Neural>> current =  postStack.poll();
                if (current.r) { //post-order = ready 4 processing
                    return (BaseNeuron<Neurons, State.Neural>) current.s; // will perform mark as visited
                }
                postStack.push(new Pair<>(true, current.s));
                Iterator<Neurons> inputs = network.getInputs((BaseNeuron<Neurons, State.Neural>) current.s);
                for (Neurons input; (input = inputs.next()) != null; ) {
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
            return outputNeuron.getComputationView(neuronVisitor.stateVisitor.stateIndex).getValue();
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

        public BUpVisitor(NeuralNetwork<State.Neural.Structure> network, BaseNeuron<Neurons, State.Neural> neuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, neuron);
            bUpStackIterator = new BUpIterator(network, neuron, pureNeuronVisitor);
            neuronVisitor = pureNeuronVisitor;

        }

        @Override
        public Value bottomUp() {
            while (bUpStackIterator.hasNext()) {
                BaseNeuron<Neurons, State.Neural> next = bUpStackIterator.next();
                next.visit(neuronVisitor);
            }
            return outputNeuron.getComputationView(neuronVisitor.stateVisitor.stateIndex).getValue();
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            neuronVisitor.visit(neuron);
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            neuronVisitor.visit(neuron);
        }
    }
}