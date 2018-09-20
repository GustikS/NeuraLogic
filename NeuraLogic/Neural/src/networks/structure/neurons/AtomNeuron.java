package networks.structure.neurons;

import constructs.template.HeadAtom;
import networks.structure.Weight;
import networks.structure.WeightedNeuron;

/**
 * Created by gusta on 8.3.17.
 */
public class AtomNeuron extends WeightedNeuron<AggregationNeuron> implements AtomFact {

    public AtomNeuron(HeadAtom head) {
        super(head.toString()); //todo really?
        this.activation = head.activation;
        this.offset = head.getOffset();
    }

    @Override
    public Weight getOffset() {
        return offset;
    }

    @Override
    public String getId() {
        return id;
    }
}