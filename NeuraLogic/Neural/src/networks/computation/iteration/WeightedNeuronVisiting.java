package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisiting;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.metadata.states.State;

public abstract class WeightedNeuronVisiting<V> extends NeuronVisiting<V> {

    public WeightedNeuronVisiting(StateVisiting<V> stateVisitor, NeuralNetwork<State.Neural.Structure> network, Neuron<Neuron, State.Neural> outputNeuron) {
        super(stateVisitor, network, outputNeuron);
    }

    /**
     * Expand neighbors and Propagate result of this neuron into the neighbours (inputs or outputs) with the corresponding edge weight AND add queue them for expansion, too.
     *
     * @param neuron
     */
    public abstract void visit(WeightedNeuron<Neuron, State.Neural> neuron);
}