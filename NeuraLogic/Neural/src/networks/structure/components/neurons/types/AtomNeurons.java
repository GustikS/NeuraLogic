package networks.structure.components.neurons.types;

import networks.structure.components.neurons.states.State;

/**
 * Just a formal annotation interface for both weighted and unweighted AtomNeurons
 */
public interface AtomNeurons<S extends State.Neural> extends AtomFact<AggregationNeuron, S> {


}