package networks.structure.components.neurons.types;

import networks.structure.components.weights.Weight;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;

/**
 * For now a useless interface, just a formal grouping for Atom and Fact neurons.
 */
public interface AtomFact<T extends Neuron,S extends State.Neural> extends Neuron<T,S> {
    Weight getOffset();
}