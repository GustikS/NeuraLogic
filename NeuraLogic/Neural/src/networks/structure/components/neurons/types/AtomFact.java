package networks.structure.components.neurons.types;

import networks.structure.components.neurons.Neurons;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

/**
 * For now a useless interface, just a formal grouping for Atom and Fact neurons.
 */
public interface AtomFact<T extends Neurons,S extends State.Neural> extends Neurons<T,S> {
    Weight getOffset();

}