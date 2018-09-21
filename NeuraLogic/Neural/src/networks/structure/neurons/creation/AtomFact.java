package networks.structure.neurons.creation;

import networks.structure.weights.Weight;
import networks.structure.neurons.Neurons;

/**
 * For now a useless interface, just a formal grouping for Atom and Fact neurons.
 */
public interface AtomFact extends Neurons {
    Weight getOffset();
}