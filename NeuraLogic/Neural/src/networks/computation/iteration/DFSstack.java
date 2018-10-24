package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisitor;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.metadata.states.State;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Logger;

public class DFSstack {
    private static final Logger LOG = Logger.getLogger(DFSstack.class.getName());

    private Deque<Neuron<Neuron, State.Computation>> stack;

    public class TopDown {

        public class StackIterator extends IterationStrategy implements networks.computation.iteration.TopDown, NeuronIterating, NeuronVisiting {

            public StackIterator(StateVisitor stateVisitor, NeuralNetwork<State.Structure> network, Neuron<Neuron, State.Computation> neuron) {
                super(network, neuron);
                stack = new ArrayDeque<>();
                stack.push(outputNeuron);
            }

            public Neuron<Neuron, State.Computation> next() {
                while (!stack.isEmpty()) {
                    Neuron<Neuron, State.Computation> pop = stack.pop();
                    if (stateVisitor.ready4activation(pop.state)) {  //careful here - it would not put the state on Stack when it should with the last call if checking the children forward instead
                        Iterator<Neuron> inputs = network.getInputs(pop);
                        for (Neuron next; (next = inputs.next()) != null; ) {
                            stack.push(next);
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
        }

        //na zkousku
        public void iterate(NeuralNetwork network, Neuron<Neuron, State.Computation> outputNeuron) {
            stack.push(outputNeuron);
            while (!stack.isEmpty()) {
                Neuron<Neuron, State.Computation> pop = stack.pop();
                //expand would go here - if not, we need 2 versions for weighted neuron here
                Iterator<Neuron> inputs = network.getInputs(pop);
                for (Neuron next; (next = inputs.next()) != null; ) {
                    //or propagate here
                    propagate(pop, next);
                    if (stateVisitor.ready4activation(next.state))
                        stack.push(next);
                }
            }
        }

    }

    /**
     * todo next do a reverse post-order (topologic sort) here
     * todo - true recursive calls would be more efficient here (double call of inputs() here) - actually it's kind of same
     *
     * @return
     */
    public class BottomUp<N extends State.Structure, V> extends networks.computation.iteration.BottomUp.PostOrder<N, V> {

        public BottomUp(NeuronVisiting<V> visitor, NeuralNetwork<N> network, Neuron<Neuron, State.Computation> neuron) {
            super(visitor, network, neuron);
            stack = new ArrayDeque<>();
            stack.push(outputNeuron);
        }

        public Neuron<Neuron, State.Computation> next() {
            stack.push(neuron);
            while (!stack.isEmpty()) {
                Neuron<T, S> pop = stack.pop();
                if (neuronVisitor.ready(pop.state)) {  //ready if no input, or it already does have a value
                    Iterator<T> inputs = network.getInputs(pop);
                    for (T next; (next = inputs.next()) != null; ) {
                        neuronVisitor.propagate(next.state.getInputSum(neuronVisitor), pop.state);
                    }
                } else {
                    Iterator<T> inputs = network.getInputs(pop);
                    for (T next; (next = inputs.next()) != null; ) {
                        if (!neuronVisitor.ready(next.state)) //it must be the opposite here
                            stack.push(next);
                    }
                }
            }
            //stack.clear(); should be empty already
            return neuron.state;
        }

        @Override
        public boolean hasNext() {
            return false;
        }
    }
}