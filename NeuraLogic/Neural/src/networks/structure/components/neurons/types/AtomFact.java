package networks.structure.components.neurons.types;

import networks.structure.components.weights.Weight;
import networks.structure.components.neurons.Neurons;

/**
 * For now a useless interface, just a formal grouping for Atom and Fact neurons.
 */
public interface AtomFact extends Neurons {
    Weight getOffset();
}