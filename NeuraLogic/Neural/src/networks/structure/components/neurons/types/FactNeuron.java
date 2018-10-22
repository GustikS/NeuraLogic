package networks.structure.components.neurons.types;

import constructs.example.ValuedFact;
import networks.computation.training.evaluation.values.Value;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.components.neurons.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class FactNeuron extends WeightedNeuron<Neuron, Value> implements AtomFact {

    public FactNeuron(ValuedFact fact, int index) {
        super(fact.toString(), index, fact.getFactValue(), fact.getOffset(), null);
        inputs = null;
        weights = null;
    }

    @Override
    public Weight getOffset() {
        return offset;
    }
}