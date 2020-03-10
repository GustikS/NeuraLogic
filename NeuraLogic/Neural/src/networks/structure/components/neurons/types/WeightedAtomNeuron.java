package networks.structure.components.neurons.types;

import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.neurons.states.State;
import networks.structure.components.weights.Weight;

/**
 * Created by gusta on 8.3.17.
 */
public class WeightedAtomNeuron<S extends State.Neural> extends WeightedNeuron<AggregationNeuron, S> implements AtomNeurons<S> {

    public WeightedAtomNeuron(String groundHead, Weight offset, int index, S state) {
        super(groundHead, index, state, offset);
    }

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }

}