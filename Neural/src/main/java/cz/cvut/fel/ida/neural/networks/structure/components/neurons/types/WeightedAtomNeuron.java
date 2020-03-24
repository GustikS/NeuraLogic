package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.exporting.Exportable;

/**
 * Created by gusta on 8.3.17.
 */
public class WeightedAtomNeuron<S extends State.Neural> extends WeightedNeuron<AggregationNeuron, S> implements AtomNeurons<S>, Exportable {

    public WeightedAtomNeuron(String groundHead, Weight offset, int index, S state) {
        super(groundHead, index, state, offset);
    }

    private WeightedAtomNeuron(){}

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }

}