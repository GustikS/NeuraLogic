package networks.computation.iteration;

import networks.computation.iteration.actions.NeuronVisitor;
import networks.structure.metadata.states.State;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Logger;

public class DFSstack {
    private static final Logger LOG = Logger.getLogger(DFSstack.class.getName());

    private Deque<Neuron<Neuron, State.Computation>> stack;

    public class TopDown<N extends State.Structure, V> extends networks.computation.iteration.TopDown<N, V> {

        public TopDown(NeuronVisitor<V> visitor, NeuralNetwork<N> network, Neuron<Neuron, State.Computation> neuron) {
            super(visitor, network, neuron);
            stack = new ArrayDeque<>();
            stack.push(outputNeuron);
        }

        public Neuron<Neuron, State.Computation> next() {
            while (!stack.isEmpty()) {
                Neuron<Neuron, State.Computation> pop = stack.pop();
                Iterator<Neuron> inputs = network.getInputs(pop);
                for (Neuron next; (next = inputs.next()) != null; ) {
                    neuronVisitor.propagate(value, next.state);
                    if (neuronVisitor.ready4activation(next))
                        stack.push(next);
                }
            }
            //stack.clear(); should be empty already
            return neuron.state;
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

    }

    /**
     * todo next do a reverse post-order (topologic sort) here
     * todo - true recursive calls would be more efficient here (double call of inputs() here)
     *
     * @return
     */
    public class BottomUp<N extends State.Structure, V> extends networks.computation.iteration.BottomUp<N, V> {

        public BottomUp(NeuronVisitor<V> visitor, NeuralNetwork<N> network, Neuron<Neuron, State.Computation> neuron) {
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