package networks.computation.iteration.actions;

import networks.structure.metadata.states.State;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;

/**
 * @param <V>
 */
public abstract class NeuronVisitor<V> {

    public StateVisitor<V> stateVisitor;

    /**
     * Check, typically using a State of the Neuron, whether this neuron is ready to propagate its result
     * @param neuron
     * @return
     */
    public abstract boolean ready4activation(Neuron neuron);

    /**
     * Transformation of the State of this neuron through its activation function into a result V (typically a Value)
     * @param neuron
     * @return
     */
    public abstract V activateOutput(Neuron neuron);

    /**
     * Expand neighbors and Propagate result of this neuron into the neighbours (inputs or outputs).
     * This call thus also needs to take care to find the neighbours itself.
     * Will call propagation between the corresponding neurons.
     * @param neuron
     */
    public abstract void expand(NeuralNetwork<State.Structure> network, Neuron neuron);

    /**
     * Expand neighbors and Propagate result of this neuron into the neighbours (inputs or outputs).
     * This call thus also needs to take care to find the neighbours itself.
     * Will call propagation between the corresponding neurons with the corresponding edge weight.
     * @param neuron
     */
    public abstract void expand(NeuralNetwork<State.Structure> network, WeightedNeuron neuron);

    /**
     * Propagate result from the neuron from into the destination neuron
     * @param from
     * @param to
     */
    public abstract void propagate(Neuron from, Neuron to);

    /**
     * Propagate result from the neuron from into the destination neuron using the information from the weight
     * @param from
     * @param to
     */
    public abstract void propagate(Neuron from, Neuron to, Weight weight);

}