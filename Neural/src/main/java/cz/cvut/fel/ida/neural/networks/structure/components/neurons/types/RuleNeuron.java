package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

/**
 * Created by gusta on 8.3.17.
 */
public class RuleNeuron<S extends State.Neural> extends BaseNeuron<AtomFact, S> implements RuleNeurons<AtomFact, S> {

    public RuleNeuron(String ruleString, int index, S state) {
        super(index, ruleString, state);
    }

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }
}