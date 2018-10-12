package networks.structure.neurons.types;

import constructs.example.ValuedFact;
import networks.evaluation.values.Value;
import networks.structure.weights.Weight;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.WeightedNeuron;

/**
 * Created by gusta on 8.3.17.
 */
public class FactNeuron extends WeightedNeuron<Neuron, Value> implements AtomFact {

    public FactNeuron(ValuedFact fact, int index) {
        super(fact.toString(), index, fact.getFactValue(), fact.getOffset(), null);
        inputs = null;
    }

    @Override
    public Weight getOffset() {
        return offset;
    }
}