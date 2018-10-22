package networks.computation.iteration;

import networks.computation.iteration.actions.NeuronVisitor;
import networks.structure.metadata.states.State;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;

public abstract class TopDown<N extends State.Structure, V> extends IterationStrategy<N, V> {

    /**
     * todo next - wrong! split into 2 separate subclasses, backprop is one topDown kind of iteration and eval is bottomUp
     * - they both implement a single iteration method
     * todo also introduce StateVisitor for each NeuronVisitor - get un/weighted inputs through DD (and maybe NetworkVisitor?)
     * Going from outputNeuron towards the leaf nodes (FactNeurons). This can go in a PRE-ORDER fashion with tail-recursion.
     *
     * @return
     */
    public TopDown(NeuronVisitor<V> neuronVisitor, NeuralNetwork<N> network, Neuron<Neuron, State.Computation> outputNeuron) {
        super(neuronVisitor, network, outputNeuron);
    }
}