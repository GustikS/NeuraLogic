package networks.structure.neurons;

import networks.structure.Weight;

/**
 * For now a useless interface, just a formal grouping for Atom and Fact neurons.
 */
public interface AtomFact extends Neurons {

    Weight getOffset();
}