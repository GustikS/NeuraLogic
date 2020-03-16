package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.algebra.weights.Weight;

/**
 * Created by gusta on 8.3.17.
 */
public class AtomNeuron<S extends State.Neural> extends BaseNeuron<AggregationNeuron, S> implements AtomNeurons<S> {

    public AtomNeuron(String groundHead, int index, S state) {
        super(index, groundHead, state);
    }

    @Override
    public Weight getOffset() {
        return null;
    }

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }
}
