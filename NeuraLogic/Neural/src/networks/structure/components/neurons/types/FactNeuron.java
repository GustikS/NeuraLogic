package networks.structure.components.neurons.types;

import constructs.example.ValuedFact;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.States;

import java.util.ArrayList;

/**
 * Created by gusta on 8.3.17.
 */
public class FactNeuron extends WeightedNeuron<Neuron, States.SimpleValue> implements AtomFact {

    public FactNeuron(ValuedFact fact, int index) {
        super(fact.toString(), index, new States.SimpleValue(fact.getFactValue()), fact.getOffset(), null);
        inputs = new ArrayList<>(0);
        weights = new ArrayList<>(0);
    }

    @Override
    public Weight getOffset() {
        return offset;
    }
}