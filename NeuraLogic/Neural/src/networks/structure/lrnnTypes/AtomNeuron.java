package networks.structure.lrnnTypes;

import constructs.template.HeadAtom;
import networks.evaluation.values.Value;
import networks.structure.Neuron;
import networks.structure.Weight;

/**
 * Created by gusta on 8.3.17.
 */
public class AtomNeuron extends Neuron<AggregationNeuron> implements AtomFact{

    public AtomNeuron(HeadAtom head) {
        super(head.toString()); //todo really?
        this.activation = head.activation;
        this.offset = head.getOffset();
    }

    @Override
    public Value evaluate() {
        return null;
    }

    @Override
    public Weight getOffset() {
        return offset;
    }

    @Override
    public Value gradient() {
        return null;
    }

    @Override
    public String getId() {
        return id;
    }
}
