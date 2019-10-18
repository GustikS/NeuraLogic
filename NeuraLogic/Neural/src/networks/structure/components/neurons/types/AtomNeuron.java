package networks.structure.components.neurons.types;

import ida.ilp.logic.Literal;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

/**
 * Created by gusta on 8.3.17.
 */
public class AtomNeuron<S extends State.Neural> extends WeightedNeuron<AggregationNeuron, S> implements AtomFact<AggregationNeuron, S> {    //todo next unweighted atomneuron...

    public AtomNeuron(Literal groundHead, Weight offset, int index, S state) {
        super(groundHead.toString(), index, state, offset);
    }

}
