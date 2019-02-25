package networks.structure.components.neurons.types;

import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;

/**
 * Just a formal anotation interface for both weighted and unwighted RuleNeurons
 */
public interface RuleNeurons<T extends Neuron, S extends State.Neural> extends Neuron<T, S> {
}
