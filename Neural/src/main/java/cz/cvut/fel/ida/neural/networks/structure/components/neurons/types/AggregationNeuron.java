package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

/**
 * Created by gusta on 8.3.17.
 */
public class AggregationNeuron<S extends State.Neural> extends BaseNeuron<RuleNeurons, S> {

    public AggregationNeuron(String groundRule, int index, S state) {
        super(index, groundRule, state);
    }

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }
}
