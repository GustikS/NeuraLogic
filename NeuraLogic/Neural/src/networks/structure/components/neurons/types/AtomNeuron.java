package networks.structure.components.neurons.types;

import constructs.template.components.HeadAtom;
import networks.structure.metadata.states.State;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;

/**
 * Created by gusta on 8.3.17.
 */
public class AtomNeuron<S extends State.Neural> extends WeightedNeuron<AggregationNeuron, S> implements AtomFact {

    public AtomNeuron(HeadAtom head, int index, S state) {
        super(head.toString(), index, state, head.getOffset());
    }

    @Override
    public final Weight getOffset() {
        return offset;
    }
}