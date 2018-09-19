package networks.structure.lrnnTypes;

import networks.structure.Weight;

/**
 * For now a useless interface, just a formal grouping for Atom and Fact neurons.
 */
public interface AtomFact extends Neural {

    Weight getOffset();

    String getId();
}