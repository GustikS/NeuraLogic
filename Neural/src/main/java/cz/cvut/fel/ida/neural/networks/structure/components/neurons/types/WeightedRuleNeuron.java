package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

public class WeightedRuleNeuron<S extends State.Neural> extends WeightedNeuron<AtomFact, S> implements RuleNeurons<AtomFact, S> {
    private static final Logger LOG = Logger.getLogger(WeightedRuleNeuron.class.getName());

    public WeightedRuleNeuron(String grounding, Weight offset, int index, S state) {
        super(grounding, index, state, offset);
    }

    @Override
    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }
}
