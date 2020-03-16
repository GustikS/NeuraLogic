package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

/**
 * Just a formal anotation interface for both weighted and unwighted RuleNeurons
 */
public interface RuleNeurons<T extends Neurons, S extends State.Neural> extends Neurons<T, S> {
}
