package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

/**
 * For now a useless interface, just a formal grouping for Atom and Fact neurons.
 */
public interface AtomFact<T extends Neurons,S extends State.Neural> extends Neurons<T,S> {
    Weight getOffset();

}