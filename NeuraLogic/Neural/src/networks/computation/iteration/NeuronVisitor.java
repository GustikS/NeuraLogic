package networks.computation.iteration;

import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;

/**
 * No recursive expansion of nodes for processing, just an immediate propagation of values to neighbours
 */
public interface NeuronVisitor {

    /**
     * Default double dispatch, some of the specific Neuron classes should be passed as an argument instead!
     * @param neuron
     */
    default void propagate(Neurons neuron) {
        System.out.println("Error: Visitor calling a default method through double dispatch.");
    }

    /**
     * Expand neighbours, Propagate result of this neuron into the neighbours (inputs or outputs).
     *
     * @param neuron
     */
    public abstract void propagate(Neuron neuron);

    /**
     * Expand neighbours and Propagate result of this neuron into the neighbours (inputs or outputs) with the corresponding edge weight.
     *
     * @param neuron
     */
    public abstract void propagate(WeightedNeuron neuron);
}