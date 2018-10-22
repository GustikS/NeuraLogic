package networks.computation.iteration;

import networks.computation.iteration.actions.NeuronVisitor;
import networks.structure.metadata.states.State;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;

import java.util.List;

public abstract class BottomUp<N extends State.Structure, V> extends IterationStrategy<N, V> {

    public BottomUp(NeuronVisitor<V> neuronVisitor, NeuralNetwork<N> network, Neuron<Neuron, State.Computation> outputNeuron) {
        super(neuronVisitor, network, outputNeuron);
    }

    /**
     * Going from leaf nodes (FactNeurons) towards the outputNeuron. Given the output neuron as a method input,
     * the corresponding recursive implementation cannot use tail-recursion! It is a POST-ORDER iteration.
     */
    public abstract class PostOrder<N extends State.Structure, V> extends BottomUp<N, V> {

        public PostOrder(NeuronVisitor<V> neuronVisitor, NeuralNetwork<N> network, Neuron<Neuron, State.Computation> outputNeuron) {
            super(neuronVisitor, network, outputNeuron);
            this.outputNeuron = outputNeuron;
        }
    }

    /**
     * Linear bottom up iteration starting from leafs.
     */
    public abstract class PreOrder<N extends State.Structure, V> extends BottomUp<N, V> {

        List<Neuron<?, ?>> leafs;

        public PreOrder(NeuronVisitor<V> neuronVisitor, NeuralNetwork<N> network, Neuron<Neuron, State.Computation> outputNeuron, List<Neuron<?, ?>> leafs) {
            super(neuronVisitor, network, outputNeuron);
            this.outputNeuron = outputNeuron;
            this.leafs = leafs;
        }
    }
}
