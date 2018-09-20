package networks.structure.neurons;

import constructs.example.ValuedFact;
import networks.evaluation.values.Value;
import networks.structure.Neuron;
import networks.structure.Weight;

/**
 * Created by gusta on 8.3.17.
 */
public class FactNeuron extends Neuron implements AtomFact {

    Value value;
    Weight predicateOffset;

    public FactNeuron(ValuedFact fact) {
        super(fact.toString());
        this.value = fact.getFactValue();
        this.predicateOffset = fact.getOffset();
        inputs = null;
        activation = null;
    }

    @Override
    public Weight getOffset() {
        return predicateOffset;
    }
}