package networks.structure.components.neurons.types;

import constructs.example.ValuedFact;
import networks.computation.iteration.NeuronVisiting;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.States;

import java.util.ArrayList;

/**
 * Created by gusta on 8.3.17.
 */
public class FactNeuron extends WeightedNeuron<BaseNeuron, States.SimpleValue> implements AtomFact<BaseNeuron, States.SimpleValue> {

    public FactNeuron(ValuedFact fact, int index, States.SimpleValue state) {
        super(fact.toString(), index, state, fact.getOffset());
        inputs = new ArrayList<>(0);
        weights = new ArrayList<>(0);
    }

    @Override
    public Weight getOffset() {
        return offset;
    }

    public void visit(NeuronVisitor.Weighted visitor){
        //there is nothing to do with fact neurons
    }

    public void visit(NeuronVisiting.Weighted visitor){
        //there is nothing to do with fact neurons
    }
}