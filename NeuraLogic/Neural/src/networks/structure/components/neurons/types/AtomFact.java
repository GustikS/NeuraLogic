package networks.structure.components.neurons.types;

import networks.structure.components.weights.Weight;
import networks.structure.components.neurons.Neuron;

/**
 * For now a useless interface, just a formal grouping for Atom and Fact neurons.
 */
public interface AtomFact extends Neuron {
    Weight getOffset();
}