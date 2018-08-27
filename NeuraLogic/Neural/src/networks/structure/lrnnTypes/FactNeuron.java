package networks.structure.lrnnTypes;

import constructs.example.ValuedFact;
import networks.evaluation.values.Value;
import networks.structure.Neuron;
import networks.structure.Weight;

/**
 * Created by gusta on 8.3.17.
 */
public class FactNeuron extends Neuron implements AtomFact {

    Value value;

    public FactNeuron(ValuedFact fact) {
        super(fact.toString());
        this.value = fact.getFactValue();
        this.offset = fact.getOffset();
        inputs = null;
        activation = null;
    }

    @Override
    public Value evaluate() {
        return value;
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
