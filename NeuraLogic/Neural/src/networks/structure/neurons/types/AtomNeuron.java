package networks.structure.neurons.types;

import constructs.template.HeadAtom;
import networks.structure.metadata.states.State;
import networks.structure.weights.Weight;
import networks.structure.neurons.WeightedNeuron;

/**
 * Created by gusta on 8.3.17.
 */
public class AtomNeuron<S extends State.Computation> extends WeightedNeuron<AggregationNeuron, S> implements AtomFact {

    public AtomNeuron(HeadAtom head, int index, S state) {
        super(head.toString(), index, state, head.getOffset(), head.activation);
    }

    @Override
    public final Weight getOffset() {
        return offset;
    }
}