package networks.structure.components.neurons.types;

import ida.ilp.logic.Literal;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

/**
 * Created by gusta on 8.3.17.
 */
public class UnweightedAtomNeuron<S extends State.Neural> extends BaseNeuron<AggregationNeuron, S> implements AtomNeurons<S> {

    public UnweightedAtomNeuron(Literal groundHead, int index, S state) {
        super(index, groundHead.toString(), state);
    }

    @Override
    public Weight getOffset() {
        return null;
    }
}
